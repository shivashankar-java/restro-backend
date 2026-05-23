package com.restro.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtService {

    private final String SECRET =
            "your-secret-key";

    public UUID getRestaurantId(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        String restaurantId =
                claims.get("restaurantId", String.class);

        return UUID.fromString(restaurantId);
    }
}
