package com.jwt.example.services;

import com.jwt.example.entities.RefreshToken;
import com.jwt.example.entities.User;
import com.jwt.example.repositories.RefreshTokenRepository;
import com.jwt.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    public long refreshTokenValidity=5*60*60*10000;

    private UserRepository userRepository;

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService( UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String userName){
        User user = userRepository.findByEmail(userName).get();
        RefreshToken refreshToken1 = user.getRefreshToken();

        if(refreshToken1==null){
            refreshToken1 = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiry(Instant.now().plusMillis(refreshTokenValidity))
                    .user(userRepository.findByEmail(userName).get())
                    .build();
        }else {
            refreshToken1.setExpiry(Instant.now().plusMillis(refreshTokenValidity));
        }
        user.setRefreshToken(refreshToken1);

        refreshTokenRepository.save(refreshToken1);

        return refreshToken1;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refreshTokenOb = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("Given token does not exist"));
        if(refreshTokenOb.getExpiry().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(refreshTokenOb);
            throw new RuntimeException("Refresh Token Expired!!");
        }
        return refreshTokenOb;
    }
}
