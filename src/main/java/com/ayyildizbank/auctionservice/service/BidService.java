package com.ayyildizbank.auctionservice.service;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.entity.Auction;
import com.ayyildizbank.auctionservice.entity.Bid;
import com.ayyildizbank.auctionservice.exception.ApplicationException;
import com.ayyildizbank.auctionservice.payload.request.BidRequest;
import com.ayyildizbank.auctionservice.payload.response.BidResponse;
import com.ayyildizbank.auctionservice.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.ayyildizbank.auctionservice.exception.ErrorEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BidService {

    private final AuctionService auctionService;
    private final BidRepository bidRepository;

    @Transactional
    public BidResponse create(BidRequest bidRequest, User loggedInUser) {
        Auction auction = auctionService.getAuction(bidRequest.getAuctionId()).orElseThrow(() -> new ApplicationException(AUCTION_DOES_NOT_EXIST, Map.of("auctionId", bidRequest.getAuctionId())));

        if (!auction.isActive()) {
            log.error("BidService.create user {} is trying to bid auction {} with bid {} but auction is already over", loggedInUser.getUsername(), bidRequest.getAuctionId(), bidRequest.getBidAmount());
            throw new ApplicationException(AUCTION_IS_OVER, Map.of("auctionId", bidRequest.getAuctionId()));
        }

        if (auction.getMinBid().compareTo(bidRequest.getBidAmount()) > 0) {
            log.error("BidService.create user {} is trying to bid auction {} with bid {} but it is below minimum bid", loggedInUser.getUsername(), bidRequest.getAuctionId(), bidRequest.getBidAmount());
            throw new ApplicationException(BELOW_MIN_BID, Map.of("minimumBid", auction.getMinBid()));
        }

        Optional<Bid> maxBid = getMaxBid(auction);
        if (maxBid.isPresent() && maxBid.get().getAmount().compareTo(bidRequest.getBidAmount()) > -1) {
            log.error("BidService.create user {} is trying to bid auction {} with bid {} but it is not the max bid", loggedInUser.getUsername(), bidRequest.getAuctionId(), bidRequest.getBidAmount());
            throw new ApplicationException(NOT_MAX_BID, Map.of("maxBid", maxBid.get().getAmount()));
        }

        Bid toSave = new Bid();
        Optional<Bid> existingBid = bidRepository.findBidByAuctionIdAndUserId(bidRequest.getAuctionId(), loggedInUser.getId());
        if (existingBid.isPresent()) {
            toSave = existingBid.get();
        } else {
            toSave.setCreatedBy(loggedInUser.getUsername());
            toSave.setAuctionId(auction.getId());
            toSave.setUserId(loggedInUser.getId());
        }
        toSave.setAmount(bidRequest.getBidAmount());
        bidRepository.save(toSave);
        return BidResponse.fromBid(toSave);


    }

    private Optional<Bid> getMaxBid(Auction auction) {
        if (Objects.isNull(auction.getBids())) {
            return Optional.empty();
        }
        return auction.getBids().stream().max(Comparator.comparing((Bid::getAmount)).thenComparing(Bid::getLastModifiedDate));
    }
}
