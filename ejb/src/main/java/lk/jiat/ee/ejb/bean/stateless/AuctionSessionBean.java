package lk.jiat.ee.ejb.bean.stateless;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lk.jiat.ee.core.model.Auction;
import lk.jiat.ee.ejb.bean.singleton.AuctionStoreBean;
import lk.jiat.ee.ejb.remote.AuctionService;
import java.util.List;

@Stateless
public class AuctionSessionBean implements AuctionService {

    @EJB
    private AuctionStoreBean auctionStore;

    @Override
    public int createAuction(Auction auction) {
        auctionStore.addAuction(auction);
        return auction.getId();
    }

    @Override
    public Auction getAuctionById(int id) {
        return auctionStore.findAuctionById(id);
    }

    @Override
    public List<Auction> getAllAuctions() {
        return auctionStore.getAllAuctions();
    }

    @Override
    public boolean updateAuctionBid(int auctionId, double newBid, String bidderEmail) {
        return auctionStore.updateAuctionBid(auctionId, newBid, bidderEmail);
    }

    @Override
    public boolean toggleAuctionStatus(int auctionId) {
        return auctionStore.toggleAuctionStatus(auctionId);
    }
}