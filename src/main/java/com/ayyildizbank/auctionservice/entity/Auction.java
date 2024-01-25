package com.ayyildizbank.auctionservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "auctions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private Long productId;

    @Column(nullable = false, updatable = false)
    private BigDecimal minBid;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy="auctionId", fetch = FetchType.LAZY)
    private Set<Bid> bids;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
}
