package com.example.stage1Hng.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JWTConfig {

    @Value("${app.jwt-secret-key}")
    private String jwtSecretKey;
    @Value("${app.jwt-expiration}")
    private int jwtExpiration;


    public String generateToken(Authentication authentication){
        String userName=authentication.getName();
        Date currentTime=new Date();
        Date expiryDate=new Date(currentTime.getTime()+ jwtExpiration);

        String token = Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(currentTime)
                .setExpiration(expiryDate)
                .signWith(getKey(),SignatureAlgorithm.HS256)
                .compact();
        return token;
    }

    public String extractUsername (String token){
        return Jwts
                .parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parse(token);
            return true;
        }catch (Exception ex){
            throw new AuthenticationCredentialsNotFoundException("Jwt was expired or incorrect");
        }
    }



    private Key getKey(){
        byte[]keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
