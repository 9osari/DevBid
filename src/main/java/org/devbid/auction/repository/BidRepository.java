package org.devbid.auction.repository;

import org.devbid.auction.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BidRepository extends JpaRepository<Bid, Long> {

}
