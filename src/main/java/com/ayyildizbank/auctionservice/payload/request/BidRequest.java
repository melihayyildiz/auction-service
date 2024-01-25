package com.ayyildizbank.auctionservice.payload.request;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.entity.Auction;
import com.ayyildizbank.auctionservice.entity.Bid;
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
public class BidRequest {

    @NotNull
    private Long auctionId;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=10, fraction=2)
    private BigDecimal bidAmount;

    public Bid toBid(User user){
        Bid bid = new Bid();
        bid.setCreatedBy(user.getUsername());
        bid.setAmount(bidAmount);
        bid.setAuctionId(auctionId);
        return bid;
    }
}
