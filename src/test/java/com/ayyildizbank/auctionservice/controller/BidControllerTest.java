package com.ayyildizbank.auctionservice.controller;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.payload.request.BidRequest;
import com.ayyildizbank.auctionservice.payload.response.BidResponse;
import com.ayyildizbank.auctionservice.payload.response.CustomResponse;
import com.ayyildizbank.auctionservice.service.BidService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BidController.class)
@WebAppConfiguration
class BidControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    BidService bidService;

    @BeforeEach
    void setUp() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        User user = new User();
        user.setUsername("sezin");
        user.setId(111L);
        user.setRoles(new HashSet<>(List.of("ROLE_SELLER")));
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void testCreateBid() throws Exception {

        BidRequest bidRequest = new BidRequest();
        bidRequest.setBidAmount(BigDecimal.TEN);
        bidRequest.setAuctionId(1L);
        BidResponse bidResponse = BidResponse.builder().bidAmount(BigDecimal.TEN).auctionId(1L).createdBy("melih").id(12L).build();
        Mockito.when(bidService.create(any(), any())).thenReturn(bidResponse);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(bidRequest);


        MvcResult mvcResult = mockMvc.perform(post("/api/bid").content(requestJson).with(csrf()).contentType("application/json"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/vnd.melih.api.v1+json"))
            .andReturn();


        String contentAsString = mvcResult.getResponse().getContentAsString();
        CustomResponse result = objectMapper.readValue(contentAsString, CustomResponse.class);

        assertEquals("success", result.getMessage());
        assertTrue(result.isSuccess());

        BidResponse responseData = objectMapper.convertValue(result.getData(), BidResponse.class);
        assertThat(responseData).isEqualToComparingFieldByField(bidResponse);
    }
}