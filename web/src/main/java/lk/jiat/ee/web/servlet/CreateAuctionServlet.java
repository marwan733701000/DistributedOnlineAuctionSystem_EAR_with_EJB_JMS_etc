package lk.jiat.ee.web.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import lk.jiat.ee.core.model.Auction;
import lk.jiat.ee.ejb.remote.AuctionService;
import java.time.format.DateTimeFormatter;
import jakarta.annotation.Resource;
import jakarta.jms.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/create-auction")
public class CreateAuctionServlet extends HttpServlet {

    @EJB
    private AuctionService auctionService;

    @Resource(lookup = "jms/MyConnectionFactory")
    private TopicConnectionFactory topicConnectionFactory;

    @Resource(lookup = "jms/MyTopic")
    private Topic topic;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        try {
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            double startingBid = Double.parseDouble(request.getParameter("startingBid"));

            if (startingBid < 1) {
                session.setAttribute("errorMessage", "Starting bid must be at least Rs. 1.00");
                response.sendRedirect("create-auction.jsp?error=true");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = LocalDateTime.parse(request.getParameter("endTime"), formatter);

            LocalDateTime minEndTime = startTime.plusMinutes(5);
            if (endTime.isBefore(minEndTime)) {
                session.setAttribute("errorMessage", "End time must be at least 5 minutes from now");
                response.sendRedirect("create-auction.jsp?error=true");
                return;
            }

            Auction auction = new Auction(productName, description, startingBid, startTime, endTime);

            int assignedId = auctionService.createAuction(auction);

            publishNewAuctionEvent(auction);

            session.setAttribute("createdAuctionId", assignedId);
            session.setAttribute("createdProductName", auction.getProductName());
            response.sendRedirect("create-auction.jsp?success=true");

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid starting bid amount");
            response.sendRedirect("create-auction.jsp?error=true");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Failed to create auction: " + e.getMessage());
            response.sendRedirect("create-auction.jsp?error=true");
        }
    }

    private void publishNewAuctionEvent(Auction auction) {
        try (TopicConnection connection = topicConnectionFactory.createTopicConnection();
             TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)) {

            TopicPublisher publisher = session.createPublisher(topic);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            AuctionEvent auctionEvent = new AuctionEvent("NEW_AUCTION", auction);
            String auctionJson = mapper.writeValueAsString(auctionEvent);

            TextMessage message = session.createTextMessage(auctionJson);
            message.setStringProperty("eventType", "NEW_AUCTION");

            publisher.publish(message);
            System.out.println("Published new auction event for: " + auction.getProductName());

        } catch (Exception e) {
            System.err.println("Failed to publish new auction event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("admin");
        response.sendRedirect((isAdmin != null && isAdmin) ? "create-auction.jsp" : "admin-login.jsp");
    }

    public static class AuctionEvent {
        private String eventType;
        private Auction auction;
        public AuctionEvent() {}
        public AuctionEvent(String eventType, Auction auction) {
            this.eventType = eventType;
            this.auction = auction;
        }
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public Auction getAuction() { return auction; }
        public void setAuction(Auction auction) { this.auction = auction; }
    }
}