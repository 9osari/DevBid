package org.devbid.auction.repository;

import org.devbid.auction.domain.Auction;
import org.devbid.auction.domain.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Optional<Auction> findById(Long auctionId);

    Optional<Auction> findByIdAndProductSellerId(Long auctionId, Long sellerId);

    @Query("SELECT a FROM Auction a " +
    "LEFT JOIN a.product b " +
    "WHERE b.seller.id = :sellerId " +
    "AND a.status != 'CANCELLED'" +
    "ORDER BY COALESCE(a.updatedAt, a.createdAt) DESC ")
    Page<Auction> findBySellerId(Long sellerId, Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "JOIN a.product p " +
            "WHERE p.seller.id = :sellerId " +
            "ORDER BY a.createdAt DESC")
    Page<Auction> findRecentBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);

    //스케줄러 경매 시작전 -> 시작
    List<Auction> findByStatusAndStartTimeBefore(AuctionStatus status, LocalDateTime startTime);

    //스케줄러 경매 시작 -> 종료
    List<Auction> findByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime endTime);

    @Query("SELECT a FROM Auction a " +
        "LEFT JOIN a.product b " +
        "WHERE a.status != :status " +
        "ORDER BY COALESCE(a.updatedAt, a.createdAt) DESC ")
    List<Auction> findAllByStatusNot(@Param("status") AuctionStatus status);

    @Query("SELECT a FROM Auction a " +
        "LEFT JOIN a.product b " +
        "WHERE a.status != 'CANCELLED'" +
        "ORDER BY a.bidCount DESC, a.updatedAt DESC ")
    List<Auction> findHotAuction();

    int countByProductSellerId(Long sellerId);
    int countByProductSellerIdAndStatus(Long sellerId, AuctionStatus status);
    int countByStatus(AuctionStatus status);
}
