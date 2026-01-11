package lk.jiat.ee.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Auction implements Serializable {
    private int id;
    private String productName;
    private String description;
    private double startingBid;
    private double currentBid;
    private String highestBidderEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active = true;

    public Auction(String productName, String description, double startingBid, LocalDateTime startTime, LocalDateTime endTime) {
        this.productName = productName;
        this.description = description;
        this.startingBid = startingBid;
        this.currentBid = startingBid;
        this.highestBidderEmail = null;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Auction() {}


    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingBid() {
        return startingBid;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public String getHighestBidderEmail() {
        return highestBidderEmail;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartingBid(double startingBid) {
        this.startingBid = startingBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public void setHighestBidderEmail(String highestBidderEmail) {
        this.highestBidderEmail = highestBidderEmail;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}