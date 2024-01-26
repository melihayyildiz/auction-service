package com.ayyildizbank.auctionservice.controller;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.payload.request.AuctionRequest;
import com.ayyildizbank.auctionservice.payload.response.CustomResponse;
import com.ayyildizbank.auctionservice.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    private final AuctionService auctionService;
    @PostMapping(produces = "application/vnd.melih.api.v1+json")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<CustomResponse> create(@Valid @RequestBody AuctionRequest auctionRequest, Authentication authentication) {
        User user = ((User)authentication.getPrincipal());
        log.info("AuctionController.create auction create request by {} as {}", user.getUsername(), auctionRequest.toString());
        return ResponseEntity.ok(CustomResponse.success(auctionService.create(auctionRequest,user)));
    }

    @PutMapping(value = "/{id}", produces = "application/vnd.melih.api.v1+json")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<CustomResponse> endAuction(@Valid @PathVariable Long id, Authentication authentication) {
        User user = ((User)authentication.getPrincipal());
        log.info("AuctionController.create auction end request by {} for id {}", user.getUsername(), id);
        return ResponseEntity.ok(CustomResponse.success(auctionService.endAuction(id,user)));
    }

    @GetMapping(produces = "application/vnd.melih.api.v1+json")
    public ResponseEntity<CustomResponse> list(Authentication authentication) {
        User user = ((User)authentication.getPrincipal());
        log.info("AuctionController.create auction list request by {}", user.getUsername());
        return ResponseEntity.ok(CustomResponse.success(auctionService.list(user)));
    }


}
