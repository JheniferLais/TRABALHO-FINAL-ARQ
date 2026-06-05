package com.sched.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sched.api.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Component
public class TokenService {

    @Value("${application.security.jwt.secret}")
    private String SECRET;

    @Value("${application.security.jwt.expiration}")
    private long EXPIRATION_SECONDS;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withIssuer("sched-api")
                .withSubject(user.getEmail())
                .withExpiresAt(LocalDateTime.now().plusSeconds(EXPIRATION_SECONDS).toInstant(ZoneOffset.of("-03:00")))
                .sign(algorithm);
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.require(algorithm).withIssuer("sched-api").build().verify(token).getSubject();
        } catch (Exception e) { return ""; }
    }
}
