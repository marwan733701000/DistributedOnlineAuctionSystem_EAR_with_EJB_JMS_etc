package lk.jiat.ee.ejb.remote;

import lk.jiat.ee.core.model.Auction;
import java.util.List;

public interface AuctionService {
    int createAuction(Auction auction);

    Auction getAuctionById(int id);
    List<Auction> getAllAuctions();
    boolean updateAuctionBid(int auctionId, double newBid, String bidderEmail);
    boolean toggleAuctionStatus(int auctionId);
}