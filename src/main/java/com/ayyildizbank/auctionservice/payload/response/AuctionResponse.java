package com.ayyildizbank.auctionservice.payload.response;

import com.ayyildizbank.auctionservice.entity.Auction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResponse {
    public Long id;
    public String createdBy;
    public BigDecimal minBid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal maxBid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String maxBidOwner;
    public long createdDate;
    public long lastModifiedDate;
    public boolean active;

    public static AuctionResponse fromAuction(Auction auction){

        return AuctionResponse.builder().id(auction.getId()).createdBy(auction.getCreatedBy()).minBid(auction.getMinBid())
            .createdDate(auction.getCreatedDate()).lastModifiedDate(auction.getLastModifiedDate()).active(auction.isActive()).build();
    }

}
