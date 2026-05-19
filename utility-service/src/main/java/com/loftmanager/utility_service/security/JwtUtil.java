package com.loftmanager.utility_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Llave sincronizada con auth-service para validar firmas
    private static final String SECRET_KEY = "NDI0NTY3ODlBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1Njc4OEE=";

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    
    public String extractRole(String token) {
            // 1. Decodificamos la clave secreta Base64 compartida del sistema de forma nativa
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode("NDI0NTY3ODlBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1Njc4OEE=");
        java.security.Key signingKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

            // 2. Construimos el parser usando esa llave decodificada directamente
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey) 
                .build()
                .parseClaimsJws(token)
                .getBody();
            
        return claims.get("rol", String.class); // Extrae el texto "ADMIN", "OPERADOR", etc.
    }

    public Long extractIdLoft(String token) {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode("NDI0NTY3ODlBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1Njc4OEE=");
        java.security.Key signingKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey) 
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        return claims.get("idLoft", Long.class); // Retorna el número de loft (ej: 1)
    }
}