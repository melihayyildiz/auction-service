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
        Auction auction = validateAndGetAuction(bidRequest.getAuctionId(), loggedInUser);
        validateBidAmount(auction, bidRequest, loggedInUser);
        Bid toSave = prepareBid(auction, bidRequest, loggedInUser);
        bidRepository.save(toSave);
        return BidResponse.fromBid(toSave);
    }

    private Auction validateAndGetAuction(Long auctionId, User loggedInUser) {
        Auction auction = auctionService.getAuction(auctionId)
            .orElseThrow(() -> new ApplicationException(AUCTION_DOES_NOT_EXIST, Map.of("auctionId", auctionId)));
        if (!auction.isActive()) {
            log.error("BidService.create user {} is trying to bid auction {} auction is already over", loggedInUser.getUsername(), auctionId);
            throw new ApplicationException(AUCTION_IS_OVER, Map.of("auctionId", auctionId));
        }
        return auction;
    }

    private void validateBidAmount(Auction auction, BidRequest bidRequest, User loggedInUser) {
        if (auction.getMinBid().compareTo(bidRequest.getBidAmount()) > 0) {
            throw new ApplicationException(BELOW_MIN_BID, Map.of("minimumBid", auction.getMinBid()));
        }
        getMaxBid(auction).ifPresent(maxBid -> {
            if (maxBid.getAmount().compareTo(bidRequest.getBidAmount()) > -1) {
                throw new ApplicationException(NOT_MAX_BID, Map.of("maxBid", maxBid.getAmount()));
            }
        });
    }

    private Bid prepareBid(Auction auction, BidRequest bidRequest, User loggedInUser) {
        return bidRepository.findBidByAuctionIdAndUserId(bidRequest.getAuctionId(), loggedInUser.getId())
            .map(bid -> updateExistingBid(bid, bidRequest))
            .orElseGet(() -> createNewBid(auction, bidRequest, loggedInUser));
    }

    private Bid updateExistingBid(Bid bid, BidRequest bidRequest) {
        bid.setAmount(bidRequest.getBidAmount());
        return bid;
    }

    private Bid createNewBid(Auction auction, BidRequest bidRequest, User loggedInUser) {
        Bid newBid = new Bid();
        newBid.setCreatedBy(loggedInUser.getUsername());
        newBid.setAuctionId(auction.getId());
        newBid.setUserId(loggedInUser.getId());
        newBid.setAmount(bidRequest.getBidAmount());
        return newBid;
    }

    private Optional<Bid> getMaxBid(Auction auction) {
        if (Objects.isNull(auction.getBids())) {
            return Optional.empty();
        }
        return auction.getBids().stream()
            .max(Comparator.comparing(Bid::getAmount).thenComparing(Bid::getLastModifiedDate));
    }
}

