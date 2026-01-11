package lk.jiat.ee.ejb.remote;
import jakarta.ejb.Remote;
import lk.jiat.ee.core.model.Bidder;
import java.util.List;

@Remote
public interface BidderService {
    boolean registerBidder(Bidder bidder);
    Bidder login(String email, String password);
    Bidder getBidderByEmail(String email);
    List<Bidder> getAllBidders();
}