package com.ayyildizbank.auctionservice.payload.response;

import com.ayyildizbank.auctionservice.entity.Bid;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
