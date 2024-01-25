package com.ayyildizbank.auctionservice.service;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.entity.Auction;
import com.ayyildizbank.auctionservice.entity.Bid;
import com.ayyildizbank.auctionservice.exception.ErrorEnum;
import com.ayyildizbank.auctionservice.payload.request.BidRequest;
import com.ayyildizbank.auctionservice.payload.response.BidResponse;
import com.ayyildizbank.auctionservice.repository.BidRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @Mock
    AuctionService auctionService;

    @Mock
    BidRepository bidRepository;

    @InjectMocks
    BidService bidService;

    @Test
    void itShould_create_bid(){

        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId(1L);
        bidRequest.setBidAmount(BigDecimal.valueOf(150L));

        User user = new User();
        user.setUsername("sezin");
        user.setId(111L);

        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setUserId(10L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(100L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Mockito.when(auctionService.getAuction(bidRequest.getAuctionId())).thenReturn(Optional.of(auction1));
        Mockito.when(bidRepository.findBidByAuctionIdAndUserId(bidRequest.getAuctionId(), user.getId())).thenReturn(Optional.empty());

        BidResponse bidResponse = bidService.create(bidRequest, user);

        ArgumentCaptor<Bid> captor = ArgumentCaptor.forClass(Bid.class);
        Mockito.verify(bidRepository, Mockito.times(1)).save(captor.capture());

        assertNull(captor.getValue().getId());
        assertEquals(captor.getValue().getAmount(), bidRequest.getBidAmount());
        assertEquals(captor.getValue().getAuctionId(), bidRequest.getAuctionId());
        assertEquals(captor.getValue().getCreatedBy(), user.getUsername());
        assertEquals(captor.getValue().getUserId(), user.getId());
        assertEquals(bidResponse.getAuctionId(), bidRequest.getAuctionId());
        assertEquals(bidResponse.getBidAmount(), bidRequest.getBidAmount());
        assertEquals(bidResponse.getCreatedBy(), user.getUsername());

    }

    @Test
    void itShouldNot_create_bid_if_auctionDoesNotExist(){

        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId(1L);
        bidRequest.setBidAmount(BigDecimal.valueOf(150L));

        User user = new User();
        user.setUsername("sezin");
        user.setId(111L);


        Mockito.when(auctionService.getAuction(bidRequest.getAuctionId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bidService.create(bidRequest, user);
        });

        String expectedMessage = ErrorEnum.AUCTION_DOES_NOT_EXIST.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }

    @Test
    void itShouldNot_create_bid_if_auction_is_over(){

        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId(1L);
        bidRequest.setBidAmount(BigDecimal.valueOf(150L));

        User user = new User();
        user.setUsername("sezin");
        user.setId(111L);

        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setUserId(10L);
        auction1.setActive(false);
        auction1.setMinBid(BigDecimal.valueOf(500L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Mockito.when(auctionService.getAuction(bidRequest.getAuctionId())).thenReturn(Optional.of(auction1));


        Exception exception = assertThrows(RuntimeException.class, () -> {
            bidService.create(bidRequest, user);
        });

        String expectedMessage = ErrorEnum.AUCTION_IS_OVER.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }

    @Test
    void itShouldNot_create_bid_if_bid_is_below_min(){

        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId(1L);
        bidRequest.setBidAmount(BigDecimal.valueOf(150L));

        User user = new User();
        user.setUsername("sezin");
        user.setId(111L);

        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setUserId(10L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(500L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Mockito.when(auctionService.getAuction(bidRequest.getAuctionId())).thenReturn(Optional.of(auction1));


        Exception exception = assertThrows(RuntimeException.class, () -> {
            bidService.create(bidRequest, user);
        });

        String expectedMessage = ErrorEnum.BELOW_MIN_BID.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }

    @Test
    void itShouldNot_create_bid_if_bid_is_below_current_max(){

        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId(1L);
        bidRequest.setBidAmount(BigDecimal.valueOf(555L));

        User user = new User();
        user.setUsername("sezin");
        user.setId(111L);

        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setUserId(10L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(500L));
        auction1.setCreatedBy("ufuk");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Bid bid3 = new Bid();
        bid3.setCreatedBy("sezin");
        bid3.setAmount(BigDecimal.valueOf(555));
        bid3.setCreatedDate(1706215118002L);


        Bid bid4 = new Bid();
        bid4.setCreatedBy("melih");
        bid4.setAmount(BigDecimal.valueOf(600));
        bid4.setCreatedDate(1706215118003L);

        auction1.setBids(List.of(bid3,bid4));

        Mockito.when(auctionService.getAuction(bidRequest.getAuctionId())).thenReturn(Optional.of(auction1));


        Exception exception = assertThrows(RuntimeException.class, () -> {
            bidService.create(bidRequest, user);
        });

        String expectedMessage = ErrorEnum.NOT_MAX_BID.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }
}