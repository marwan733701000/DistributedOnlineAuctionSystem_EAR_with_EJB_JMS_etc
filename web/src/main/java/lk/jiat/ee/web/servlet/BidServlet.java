package lk.jiat.ee.web.servlet;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.jms.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lk.jiat.ee.core.model.Auction;
import lk.jiat.ee.core.model.Bid;
import lk.jiat.ee.core.model.Bidder;
import lk.jiat.ee.ejb.remote.AuctionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/bid")
public class BidServlet extends HttpServlet {
    @EJB
    private AuctionService auctionService;

    @Resource(lookup = "jms/MyConnectionFactory")
    private TopicConnectionFactory topicConnectionFactory;

    @Resource(lookup = "jms/MyTopic")
    private Topic auctionTopic;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();

        try {
            HttpSession session = request.getSession();
            Bidder loggedInBidder = (Bidder) session.getAttribute("bidder");

            if (loggedInBidder == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseData.put("success", false);
                responseData.put("message", "Please log in to place a bid");
                response.getWriter().write(mapper.writeValueAsString(responseData));
                return;
            }

            int auctionId = Integer.parseInt(request.getParameter("auctionId"));
            double bidAmount = Double.parseDouble(request.getParameter("bidAmount"));

            Auction auction = auctionService.getAuctionById(auctionId);
            if (auction == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                responseData.put("success", false);
                responseData.put("message", "Auction not found");
                response.getWriter().write(mapper.writeValueAsString(responseData));
                return;
            }

            if (!auction.isActive()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("success", false);
                responseData.put("message", "Auction is not open for bids");
                response.getWriter().write(mapper.writeValueAsString(responseData));
                return;
            }

            if (LocalDateTime.now().isAfter(auction.getEndTime())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("success", false);
                responseData.put("message", "Auction has already ended");
                response.getWriter().write(mapper.writeValueAsString(responseData));
                return;
            }

            double currentBid = auction.getCurrentBid();
            double minValidBid = currentBid + 2;

            if (bidAmount < minValidBid) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseData.put("success", false);
                responseData.put("message", "Invalid bid amount. Must be at least Rs. " + String.format("%.2f", minValidBid));
                response.getWriter().write(mapper.writeValueAsString(responseData));
                return;
            }

            boolean updateSuccess = auctionService.updateAuctionBid(auctionId, bidAmount, loggedInBidder.getEmail());

            if (!updateSuccess) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseData.put("success", false);
                responseData.put("message", "Failed to update bid. Please try again.");
                response.getWriter().write(mapper.writeValueAsString(responseData));
                return;
            }

            try (TopicConnection connection = topicConnectionFactory.createTopicConnection();
                 TopicSession jmsSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)) {

                TopicPublisher publisher = jmsSession.createPublisher(auctionTopic);

                Bid bid = new Bid(auctionId, bidAmount, loggedInBidder.getEmail());
                String bidJson = mapper.writeValueAsString(bid);
                TextMessage message = jmsSession.createTextMessage(bidJson);

                publisher.publish(message);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            responseData.put("success", true);
            responseData.put("message", "Bid placed successfully!");
            responseData.put("newBid", bidAmount);
            response.getWriter().write(mapper.writeValueAsString(responseData));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseData.put("success", false);
            responseData.put("message", "Invalid input format");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (JMSException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("success", false);
            responseData.put("message", "Error publishing bid update");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseData.put("success", false);
            responseData.put("message", "An unexpected error occurred");
            response.getWriter().write(mapper.writeValueAsString(responseData));
        }
    }
}