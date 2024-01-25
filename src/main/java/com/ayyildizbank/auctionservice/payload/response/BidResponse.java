package com.ayyildizbank.auctionservice.payload.response;

import com.ayyildizbank.auctionservice.entity.Auction;
import com.ayyildizbank.auctionservice.entity.Bid;
import com.ayyildizbank.auctionservice.payload.request.BidRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BidResponse {
    public Long id;
    public long auctionId;
    public String createdBy;
    public BigDecimal bidAmount;

    public static BidResponse fromBid(Bid bid){

        return BidResponse.builder().id(bid.getId()).auctionId(bid.getAuctionId()).createdBy(bid.getCreatedBy())
            .bidAmount(bid.getAmount()).build();
    }
}
