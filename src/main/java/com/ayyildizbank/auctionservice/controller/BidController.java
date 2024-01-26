package com.ayyildizbank.auctionservice.controller;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.payload.request.AuctionRequest;
import com.ayyildizbank.auctionservice.payload.request.BidRequest;
import com.ayyildizbank.auctionservice.payload.response.CustomResponse;
import com.ayyildizbank.auctionservice.service.AuctionService;
import com.ayyildizbank.auctionservice.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bid")
@RequiredArgsConstructor
@Slf4j
public class BidController {

    private final BidService bidService;

    @PostMapping(produces = "application/vnd.melih.api.v1+json")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CustomResponse> create(@Valid @RequestBody BidRequest bidRequest, Authentication authentication) {
        User user = ((User)authentication.getPrincipal());
        log.info("BidController.create bid create request by {} as {}", user.getUsername(), bidRequest.toString());
        return ResponseEntity.ok(CustomResponse.success(bidService.create(bidRequest,user)));
    }
}
