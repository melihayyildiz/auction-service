package com.ayyildizbank.auctionservice.service;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.entity.Auction;
import com.ayyildizbank.auctionservice.entity.Bid;
import com.ayyildizbank.auctionservice.exception.ApplicationException;
import com.ayyildizbank.auctionservice.exception.ErrorEnum;
import com.ayyildizbank.auctionservice.payload.request.AuctionRequest;
import com.ayyildizbank.auctionservice.payload.response.AuctionResponse;
import com.ayyildizbank.auctionservice.payload.response.CustomResponse;
import com.ayyildizbank.auctionservice.repository.AuctionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    AuctionRepository auctionRepository;

    @InjectMocks
    AuctionService auctionService;

    @Test
    void itShould_create_auction() {

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setProductId(123L);
        auctionRequest.setMinBid(BigDecimal.valueOf(100L));


        Mockito.when(auctionRepository.existsByProductId(auctionRequest.getProductId())).thenReturn(false);

        auctionService.create(auctionRequest, user);

        ArgumentCaptor<Auction> captor = ArgumentCaptor.forClass(Auction.class);
        Mockito.verify(auctionRepository, Mockito.times(1)).save(captor.capture());
        assertEquals(user.getId(), captor.getValue().getUserId());
        assertEquals(auctionRequest.getMinBid(), captor.getValue().getMinBid());
        assertEquals(auctionRequest.getProductId(), captor.getValue().getProductId());
        assertTrue(captor.getValue().isActive());
    }

    @Test
    void itShouldNot_create_auction_if_productIdExists() {

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setProductId(123L);
        auctionRequest.setMinBid(BigDecimal.valueOf(100L));

        Mockito.when(auctionRepository.existsByProductId(auctionRequest.getProductId())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            auctionService.create(auctionRequest, user);
        });

        String expectedMessage = ErrorEnum.AUCTION_WITH_PRODUCT_EXISTS.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }

    @Test
    void itShould_list_auctions_with_maxbid_when_role_is_seller() {
        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(100L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Bid bid1 = new Bid();
        bid1.setCreatedBy("sezin");
        bid1.setAmount(BigDecimal.valueOf(150L));
        bid1.setCreatedDate(1706215118001L);

        Bid bid5 = new Bid();
        bid5.setCreatedBy("ufuk");
        bid5.setAmount(BigDecimal.valueOf(120L));
        bid5.setCreatedDate(1706215118001L);

        Bid bid2 = new Bid();
        bid2.setCreatedBy("erol");
        bid2.setAmount(BigDecimal.valueOf(150L));
        bid2.setCreatedDate(1706215118002L);

        auction1.setBids(List.of(bid1, bid2, bid5));

        Auction auction2 = new Auction();
        auction2.setId(2L);
        auction2.setActive(true);
        auction2.setMinBid(BigDecimal.valueOf(80L));
        auction2.setCreatedBy("serdar");
        auction2.setLastModifiedDate(1706215118399L);
        auction2.setCreatedDate(1706215118001L);

        Bid bid3 = new Bid();
        bid3.setCreatedBy("seher");
        bid3.setAmount(BigDecimal.valueOf(100L));
        bid3.setCreatedDate(1706215118002L);


        Bid bid4 = new Bid();
        bid4.setCreatedBy("melih");
        bid4.setAmount(BigDecimal.valueOf(200L));
        bid4.setCreatedDate(1706215118003L);

        auction2.setBids(List.of(bid3, bid4));

        List<Auction> auctions = List.of(auction1, auction2);
        Mockito.when(auctionRepository.findAll()).thenReturn(List.of(auction1, auction2));

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);
        user.setRoles(new HashSet<>(List.of("ROLE_SELLER")));
        List<AuctionResponse> list = auctionService.list(user);

        assertEquals(auctions.size(), list.size());
        assertEquals("sezin", list.get(0).getMaxBidOwner());
        assertEquals("melih", list.get(1).getMaxBidOwner());
        assertEquals(BigDecimal.valueOf(150L), list.get(0).getMaxBid());
        assertEquals(BigDecimal.valueOf(200L), list.get(1).getMaxBid());

    }

    @Test
    void itShould_list_auctions_without_maxbid_when_role_is_not_seller_and_auction_is_not_expired() {
        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(100L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Bid bid1 = new Bid();
        bid1.setCreatedBy("sezin");
        bid1.setAmount(BigDecimal.valueOf(150L));
        bid1.setCreatedDate(1706215118001L);

        Bid bid5 = new Bid();
        bid5.setCreatedBy("ufuk");
        bid5.setAmount(BigDecimal.valueOf(120L));
        bid5.setCreatedDate(1706215118001L);

        Bid bid2 = new Bid();
        bid2.setCreatedBy("erol");
        bid2.setAmount(BigDecimal.valueOf(150L));
        bid2.setCreatedDate(1706215118002L);

        auction1.setBids(List.of(bid1, bid2, bid5));

        Auction auction2 = new Auction();
        auction2.setId(2L);
        auction2.setActive(true);
        auction2.setMinBid(BigDecimal.valueOf(80L));
        auction2.setCreatedBy("serdar");
        auction2.setLastModifiedDate(1706215118399L);
        auction2.setCreatedDate(1706215118001L);

        Bid bid3 = new Bid();
        bid3.setCreatedBy("seher");
        bid3.setAmount(BigDecimal.valueOf(100L));
        bid3.setCreatedDate(1706215118002L);


        Bid bid4 = new Bid();
        bid4.setCreatedBy("melih");
        bid4.setAmount(BigDecimal.valueOf(200L));
        bid4.setCreatedDate(1706215118003L);

        auction2.setBids(List.of(bid3, bid4));

        List<Auction> auctions = List.of(auction1, auction2);
        Mockito.when(auctionRepository.findAll()).thenReturn(List.of(auction1, auction2));

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);
        user.setRoles(new HashSet<>(List.of("ROLE_BUYER")));
        List<AuctionResponse> list = auctionService.list(user);

        assertEquals(auctions.size(), list.size());
        assertNull(list.get(0).getMaxBidOwner());
        assertNull(list.get(1).getMaxBidOwner());
        assertNull(list.get(0).getMaxBid());
        assertNull(list.get(1).getMaxBid());

    }
    @Test
    void itShould_end_auction() {

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);

        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setUserId(111L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(100L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);
        Bid bid5 = new Bid();
        bid5.setCreatedBy("ufuk");
        bid5.setAmount(BigDecimal.valueOf(120L));
        bid5.setCreatedDate(1706215118001L);

        Bid bid2 = new Bid();
        bid2.setCreatedBy("erol");
        bid2.setAmount(BigDecimal.valueOf(150L));
        bid2.setCreatedDate(1706215118002L);

        auction1.setBids(List.of(bid2, bid5));

        Mockito.when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction1));

        AuctionResponse auctionResponse = auctionService.endAuction(1L, user);

        ArgumentCaptor<Auction> captor = ArgumentCaptor.forClass(Auction.class);
        Mockito.verify(auctionRepository, Mockito.times(1)).save(captor.capture());
        assertFalse(captor.getValue().isActive());
        assertFalse(auctionResponse.isActive());
        assertEquals("erol", auctionResponse.getMaxBidOwner());
        assertEquals(BigDecimal.valueOf(150L), auctionResponse.getMaxBid());


    }

    @Test
    void itShouldNot_endAuction_ifAuctionDoesNotExist() {

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);

        Auction auction1 = new Auction();
        auction1.setId(1L);
        auction1.setUserId(999L);
        auction1.setActive(true);
        auction1.setMinBid(BigDecimal.valueOf(100L));
        auction1.setCreatedBy("melih");
        auction1.setLastModifiedDate(1706215118392L);
        auction1.setCreatedDate(1706215118000L);

        Mockito.when(auctionRepository.findById(1L)).thenReturn(Optional.of(auction1));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            auctionService.endAuction(1L, user);
        });

        String expectedMessage = ErrorEnum.NO_PERMISSION.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }

    @Test
    void itShouldNot_endAuction_ifAuctionDoesNotBelongToUser() {

        User user = new User();
        user.setUsername("melih");
        user.setId(111L);

        Mockito.when(auctionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            auctionService.endAuction(1L, user);
        });

        String expectedMessage = ErrorEnum.AUCTION_DOES_NOT_EXIST.getMessage();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals("ApplicationException", exception.getClass().getSimpleName());

    }

}