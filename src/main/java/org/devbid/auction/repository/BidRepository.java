package org.devbid.auction.repository;

import org.devbid.auction.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BidRepository extends JpaRepository<Bid, Long> {
    int countByBidderId(Long bidderId);

    @Query("SELECT b FROM Bid b WHERE b.bidder.id = :bidderId ORDER BY b.createdAt DESC")
    List<Bid> findRecentByBidderId(@Param("bidderId") Long bidderId);

    @Query("SELECT b FROM Bid b " +
            "JOIN FETCH b.auction a " +
            "JOIN FETCH a.product p " +
            "WHERE b.bidder.id = :userId " +
            "AND b.type = 'BUYOUT' " +
            "ORDER BY b.createdAt DESC " +
            "LIMIT 5")
    List<Bid> findRecentBuyOutByUserId(@Param("userId") Long userId);
}
