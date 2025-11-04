package org.devbid.auction.repository;

import org.devbid.auction.domain.Auction;
import org.devbid.auction.dto.AuctionListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT a FROM Auction a JOIN FETCH a.product")
    List<Auction> findAllAuctions();

    Optional<Auction> findById(Long auctionId);
}
