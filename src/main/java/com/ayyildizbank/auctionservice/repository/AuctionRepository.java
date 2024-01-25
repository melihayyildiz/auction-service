package com.ayyildizbank.auctionservice.repository;

import com.ayyildizbank.auctionservice.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
