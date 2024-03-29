package com.ayyildizbank.auctionservice.service;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.entity.Auction;
import com.ayyildizbank.auctionservice.entity.Bid;
import com.ayyildizbank.auctionservice.exception.ApplicationException;
import com.ayyildizbank.auctionservice.payload.request.AuctionRequest;
import com.ayyildizbank.auctionservice.payload.response.AuctionResponse;
import com.ayyildizbank.auctionservice.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ayyildizbank.auctionservice.exception.ErrorEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;

    @Transactional
    public AuctionResponse create(AuctionRequest auctionRequest, User user) {
        if (auctionRepository.existsByProductId(auctionRequest.getProductId())) {
            log.error("AuctionService.create auction with product {} already exists", auctionRequest.getProductId());
            throw new ApplicationException(AUCTION_WITH_PRODUCT_EXISTS, Map.of("productId", auctionRequest.getProductId()));
        }
        Auction auction = auctionRequest.toAuction(user);
        auction.setActive(true);
        auctionRepository.save(auction);
        log.info("AuctionService.create auction {} is saved successfully", auction);
        return AuctionResponse.fromAuction(auction);
    }

    @Transactional
    public AuctionResponse endAuction(Long auctionId, User user) {

        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new ApplicationException(AUCTION_DOES_NOT_EXIST, Map.of("auctionId", auctionId)));

        if (!auction.getUserId().equals(user.getId())) {
            log.error("AuctionService.endAuction auction {} does not belong to user {}", auctionId, user.getUsername());
            throw new ApplicationException(NO_PERMISSION);
        }
        if (auction.isActive()) {
            auction.setActive(false);
            auctionRepository.save(auction);
        } else {
            log.warn("AuctionService.endAuction auction {} is already deactivated {}", auctionId, user.getUsername());
        }

        log.info("AuctionService.endAuction auction {} is ended successfully", auction);
        return convertToAuctionResponse(auction, user);
    }

    @Transactional
    public List<AuctionResponse> list(User user) {
        return auctionRepository.findAll().stream().map(auction -> convertToAuctionResponse(auction, user)).toList();
    }

    public Optional<Auction> getAuction(Long auctionId) {
        return auctionRepository.findById(auctionId);
    }

    private AuctionResponse convertToAuctionResponse(Auction auction, User user) {
        AuctionResponse auctionResponse = AuctionResponse.fromAuction(auction);
        if( !auction.isActive() || user.getRoles().contains("ROLE_SELLER")){
            Optional<Bid> max = auction.getBids().stream().max(Comparator.comparing((Bid::getAmount)).thenComparing(Bid::getLastModifiedDate));
            if (max.isPresent()) {
                auctionResponse.setMaxBidOwner(max.get().getCreatedBy());
                auctionResponse.setMaxBid(max.get().getAmount());
            }
        }
        return auctionResponse;
    }
}
