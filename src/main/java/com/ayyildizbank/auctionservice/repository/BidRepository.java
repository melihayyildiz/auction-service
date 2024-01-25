package com.ayyildizbank.auctionservice.repository;

import com.ayyildizbank.auctionservice.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findBidByAuctionIdAndUserId(Long userId, Long auctionId);
}
