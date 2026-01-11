package lk.jiat.ee.ejb.bean.stateless;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lk.jiat.ee.core.model.Bidder;
import lk.jiat.ee.core.model.Validations;
import lk.jiat.ee.ejb.bean.singleton.BidderStoreBean;
import lk.jiat.ee.ejb.remote.BidderService;
import java.util.List;

@Stateless
public class BidderSessionBean implements BidderService {

    @EJB
    private BidderStoreBean bidderStore;

    @Override
    public boolean registerBidder(Bidder bidder){
        if (!Validations.isEmailValid(bidder.getEmail())){return false;}
        if (!Validations.isPasswordValid(bidder.getPassword())){return false;}

        if (bidderStore.findBidderByEmail(bidder.getEmail()) != null){
            return false;
        }
        bidderStore.addBidder(bidder);
        return true;
    }

    @Override
    public Bidder login(String email, String password) {
        Bidder b = bidderStore.findBidderByEmail(email);
        if (b != null && b.getPassword().equals(password)) {
            return b;
        }
        return null;
    }

    @Override
    public Bidder getBidderByEmail(String email) {
        return bidderStore.findBidderByEmail(email);
    }

    @Override
    public List<Bidder> getAllBidders() {
        return bidderStore.getAllBidders();
    }
}
