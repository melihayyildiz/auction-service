package com.ayyildizbank.auctionservice.controller;

import com.ayyildizbank.auctionservice.auth.model.User;
import com.ayyildizbank.auctionservice.payload.request.LoginRequest;
import com.ayyildizbank.auctionservice.payload.response.CustomResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auction")
@RequiredArgsConstructor
@Slf4j
public class AuctionController {

    @PostMapping(value = "/login", produces = "application/vnd.melih.api.v1+json")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<CustomResponse> login(@Valid @RequestBody LoginRequest loginRequest, @RequestAttribute User loggedInUser) {
        log.info("AuthController.login login attempt for {}", loginRequest.getUsername());
        return ResponseEntity.ok(CustomResponse.success());
    }

    @PostMapping(value = "/login2", produces = "application/vnd.melih.api.v1+json")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<CustomResponse> login2(@Valid @RequestBody LoginRequest loginRequest, @RequestAttribute User loggedInUser) {
        log.info("AuthController.login login attempt for {}", loginRequest.getUsername());
        return ResponseEntity.ok(CustomResponse.success());
    }

}
