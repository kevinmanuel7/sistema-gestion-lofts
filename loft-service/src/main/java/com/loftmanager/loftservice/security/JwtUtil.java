package com.loftmanager.loftservice.security;

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
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraemos el rol para que Spring Security sepa si es ADMIN
    public String extractRole(String token) {
        return extractAllClaims(token).get("rol", String.class); // Asegúrate de que el key coincida con tu Auth-Service
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
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
}