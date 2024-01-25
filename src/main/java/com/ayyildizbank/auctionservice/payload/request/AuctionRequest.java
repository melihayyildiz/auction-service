package com.ayyildizbank.auctionservice.payload.request;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.entity.Auction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class AuctionRequest {

    @NotNull
    private Long productId;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=10, fraction=2)
    private BigDecimal minBid;

    public Auction toAuction(User user){
        Auction auction = new Auction();
        auction.setProductId(productId);
        auction.setMinBid(minBid);
        auction.setUserId(user.getId());
        return auction;
    }
}
