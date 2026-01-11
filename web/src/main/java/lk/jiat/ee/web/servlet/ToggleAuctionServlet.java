package lk.jiat.ee.web.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Resource;
import jakarta.jms.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.jiat.ee.ejb.remote.AuctionService;
import lk.jiat.ee.core.model.Auction;

import java.io.IOException;

@WebServlet("/toggle-auction")
public class ToggleAuctionServlet extends HttpServlet {

    @EJB
    private AuctionService auctionService;

    @Resource(lookup = "jms/MyConnectionFactory")
    private TopicConnectionFactory topicConnectionFactory;

    @Resource(lookup = "jms/MyTopic")
    private Topic topic;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int auctionId = Integer.parseInt(request.getParameter("id"));

            boolean success = auctionService.toggleAuctionStatus(auctionId);

            if (success) {
                Auction auction = auctionService.getAuctionById(auctionId);

                publishAuctionStatusUpdate(auction);

                response.getWriter().write("{\"success\": true, \"active\": " + auction.isActive() + "}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Auction not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Error toggling auction status\"}");
        }
    }

    private void publishAuctionStatusUpdate(Auction auction) {
        try (TopicConnection connection = topicConnectionFactory.createTopicConnection();
             TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE)) {

            TopicPublisher publisher = session.createPublisher(topic);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            AuctionStatusUpdate update = new AuctionStatusUpdate(auction.getId(), auction.isActive());
            String updateJson = mapper.writeValueAsString(update);

            TextMessage message = session.createTextMessage(updateJson);
            message.setStringProperty("eventType", "AUCTION_STATUS_UPDATE");

            publisher.publish(message);
            System.out.println("Published auction status update for ID: " + auction.getId());

        } catch (Exception e) {
            System.err.println("Failed to publish auction status update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class AuctionStatusUpdate {
        private String type = "AUCTION_STATUS_UPDATE";
        private int auctionId;
        private boolean active;
        public AuctionStatusUpdate(int auctionId, boolean active) {
            this.auctionId = auctionId;
            this.active = active;
        }
        public String getType() { return type; }
        public int getAuctionId() { return auctionId; }
        public boolean isActive() { return active; }
    }
}