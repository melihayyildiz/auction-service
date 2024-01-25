package com.ayyildizbank.auctionservice.repository;

import com.ayyildizbank.auctionservice.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
}
