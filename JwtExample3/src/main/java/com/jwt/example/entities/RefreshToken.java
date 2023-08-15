package com.jwt.example.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name="refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId;
    private String refreshToken;
    private Instant expiry;
    @OneToOne
    private User user;
}
