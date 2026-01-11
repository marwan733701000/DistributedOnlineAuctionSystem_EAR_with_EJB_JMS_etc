package lk.jiat.ee.ejb.bean.singleton;

import jakarta.ejb.Singleton;
import lk.jiat.ee.core.model.Auction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Singleton
public class AuctionStoreBean {
    private final List<Auction> auctionList = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public void addAuction(Auction auction) {
        lock.writeLock().lock();
        try {
            auction.setId(idGenerator.getAndIncrement());
            auctionList.add(auction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Auction findAuctionById(int id) {
        lock.readLock().lock();
        try {
            for (Auction auction : auctionList) {
                if (auction.getId() == id) {
                    return auction;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Auction> getAllAuctions() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(auctionList);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean updateAuctionBid(int auctionId, double newBid, String bidderEmail) {
        lock.writeLock().lock();
        try {
            for (Auction auction : auctionList) {
                if (auction.getId() == auctionId) {
                    if (newBid > auction.getCurrentBid()) {
                        auction.setCurrentBid(newBid);
                        auction.setHighestBidderEmail(bidderEmail);
                        return true;
                    }
                    return false;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean toggleAuctionStatus(int auctionId) {
        lock.writeLock().lock();
        try {
            for (Auction auction : auctionList) {
                if (auction.getId() == auctionId) {
                    auction.setActive(!auction.isActive());
                    return true;
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
