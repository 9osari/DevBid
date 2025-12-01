package org.devbid.auction.repository;

import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    @Query("SELECT a FROM Auction a JOIN FETCH a.product")
    List<Auction> findAllAuctions();

    Optional<Auction> findById(Long auctionId);

    Optional<Auction> findByIdAndProductSellerId(Long auctionId, Long sellerId);

    @Query("SELECT a FROM Auction a " +
    "LEFT JOIN a.product b " +
    "WHERE b.seller.id = :sellerId " +
    "AND a.status != 'CANCELLED'" +
    "ORDER BY COALESCE(a.updatedAt, a.createdAt) DESC ")
    Page<Auction> findBySellerId(Long sellerId, Pageable pageable);

    //스케줄러 경매 시작전 -> 시작
    List<Auction> findByStatusAndStartTimeBefore(AuctionStatus status, LocalDateTime startTime);

    //스케줄러 경매 시작 -> 종료
    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime endTime);
}
