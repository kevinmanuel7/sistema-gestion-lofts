package com.loftmanager.auth_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.loftmanager.auth_service.model.Usuario;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "NDI0NTY3ODlBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1Njc4OEE="; 

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String generateToken(Usuario usuario) {
        Map<String, Object> extraClaims = new HashMap<>();
        
        // Sincronizado con el utility-service: Pasamos el rol limpio (ADMIN, OPERADOR, CLIENTE)
        extraClaims.put("rol", usuario.getRol().getNombreRol()); 
        if (usuario.getIdLoft() != null) {
            extraClaims.put("idLoft", usuario.getIdLoft());
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            usuario.getUsername(),
            usuario.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol()))
        );
        
        // Llamamos al método constructor pasándole los claims cargados con el rol
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}