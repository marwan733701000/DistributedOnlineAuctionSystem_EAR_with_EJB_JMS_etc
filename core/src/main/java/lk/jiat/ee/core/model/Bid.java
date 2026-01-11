package lk.jiat.ee.core.model;

import java.io.Serializable;

public class Bid implements Serializable {
    private int auctionId;
    private double amount;
    private String bidderEmail;

    public Bid() {}

    public Bid(int auctionId, double amount) {
        this.auctionId = auctionId;
        this.amount = amount;
    }

    public Bid(int auctionId, double amount, String bidderEmail) {
        this.auctionId = auctionId;
        this.amount = amount;
        this.bidderEmail = bidderEmail;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBidderEmail() {
        return bidderEmail;
    }

    public void setBidderEmail(String bidderEmail) {
        this.bidderEmail = bidderEmail;
    }
}