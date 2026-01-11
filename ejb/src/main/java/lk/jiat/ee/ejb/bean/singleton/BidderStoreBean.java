package lk.jiat.ee.ejb.bean.singleton;

import jakarta.ejb.Singleton;
import lk.jiat.ee.core.model.Bidder;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class BidderStoreBean {

    private final List<Bidder> bidderList = new ArrayList<>();

    public void addBidder(Bidder bidder) {
        bidderList.add(bidder);
    }

    public Bidder findBidderByEmail(String email) {
        for (Bidder b : bidderList) {
            if (b.getEmail().equalsIgnoreCase(email)) {
                return b;
            }
        }
        return null;
    }

    public List<Bidder> getAllBidders() {
        return new ArrayList<>(bidderList);
    }
}
