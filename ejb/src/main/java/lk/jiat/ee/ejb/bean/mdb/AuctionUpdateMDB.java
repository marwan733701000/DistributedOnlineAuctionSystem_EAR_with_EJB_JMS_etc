package lk.jiat.ee.ejb.bean.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;
import lk.jiat.ee.core.model.Bid;
import lk.jiat.ee.core.model.Auction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.jiat.ee.core.websocket.AuctionWebSocket;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/MyTopic")
})
public class AuctionUpdateMDB implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String messageContent = ((TextMessage) message).getText();
                String eventType = message.getStringProperty("eventType");

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                if ("NEW_AUCTION".equals(eventType)) {
                    AuctionEvent auctionEvent = mapper.readValue(messageContent, AuctionEvent.class);
                    Auction newAuction = auctionEvent.getAuction();
                    NewAuctionNotification notification = new NewAuctionNotification(newAuction);
                    String newAuctionJson = mapper.writeValueAsString(notification);

                    AuctionWebSocket.broadcastNewAuction(newAuctionJson);
                    System.out.println("Broadcasted new auction: " + newAuction.getProductName() + " (ID: " + newAuction.getId() + ")");

                } else if ("AUCTION_STATUS_UPDATE".equals(eventType)) {
                    AuctionStatusUpdate update = mapper.readValue(messageContent, AuctionStatusUpdate.class);
                    String updateJson = mapper.writeValueAsString(update);
                    AuctionWebSocket.broadcast(updateJson);
                    System.out.println("Broadcasted auction status update for ID: " + update.getAuctionId());
                } else {
                    Bid bid = mapper.readValue(messageContent, Bid.class);
                    AuctionWebSocket.broadcast(messageContent);
                    System.out.println("Received bid update: " + bid.getAmount() + " for auction ID " + bid.getAuctionId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class AuctionEvent {
        private String eventType;
        private Auction auction;
        public AuctionEvent() {}
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        public Auction getAuction() { return auction; }
        public void setAuction(Auction auction) { this.auction = auction; }
    }
    public static class NewAuctionNotification {
        private String type = "NEW_AUCTION";
        private Auction auction;
        public NewAuctionNotification(Auction auction) {
            this.auction = auction;
        }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Auction getAuction() { return auction; }
        public void setAuction(Auction auction) { this.auction = auction; }
    }
    public static class AuctionStatusUpdate {
        private String type;
        private int auctionId;
        private boolean active;
        public String getType() { return type; }
        public int getAuctionId() { return auctionId; }
        public boolean isActive() { return active; }
    }
}