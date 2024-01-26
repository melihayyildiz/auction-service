package com.ayyildizbank.auctionservice.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.HashSet;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private HashSet<String> roles;
}