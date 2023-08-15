package com.jwt.example.controllers;

import com.jwt.example.entities.RefreshToken;
import com.jwt.example.entities.User;
import com.jwt.example.models.JwtRequest;
import com.jwt.example.models.JwtResponse;
import com.jwt.example.models.RefreshTokenRequest;
import com.jwt.example.security.JwtHelper;
import com.jwt.example.services.RefreshTokenService;
import com.jwt.example.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager manager;
    private final JwtHelper helper;
    private final UserService userService;
    private RefreshTokenService refreshTokenService;

    public AuthController(UserDetailsService userDetailsService, AuthenticationManager manager, JwtHelper helper, UserService userService, RefreshTokenService refreshTokenService) {
        this.userDetailsService = userDetailsService;
        this.manager = manager;
        this.helper = helper;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        this.doAuthenticate(request.getEmail(), request.getPassword());


        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = helper.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .refreshToken(refreshToken.getRefreshToken())
                .username(userDetails.getUsername()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }
    }
    @PostMapping("/refresh")
    public JwtResponse refreshJwtToken(@RequestBody RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        String token = this.helper.generateToken(user);

        return JwtResponse.builder().refreshToken(refreshToken.getRefreshToken())
                .jwtToken(token)
                .username(user.getEmail())
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }

    @PostMapping("/create-user")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

}
