package com.inventory.inventory_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "NDI0NTY3ODlBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1Njc4OEE=";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token); // Esto lanza excepciones específicas
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("Token expirado: " + e.getMessage());
        } catch (io.jsonwebtoken.SignatureException e) {
            System.out.println("Firma inválida: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("Token mal formado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error desconocido al validar: " + e.getClass().getName() + " - " + e.getMessage());
        }
        return false;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }
}