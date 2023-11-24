package com.mqTool.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {
    private static final String PUBLIC_KEY_PATH = "public_key.pem";
    private static final String PRIVATE_KEY_PATH = "private_key.pem";
    private final static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public static Claims parseToken(String jwtString) throws IOException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        return Jwts.parser()
                .setSigningKey(getPublicKey()).build()
                .parseClaimsJws(jwtString).getBody();
    }

    public static String getOrCreateAccessToken(String jwtString, String currentUser) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException {
        Claims claims;
        if (jwtString != null && !jwtString.isEmpty()) {
            try{
                claims = parseToken(jwtString);
                return jwtString;
            }catch(ExpiredJwtException e){
                logger.error("Jwt token expired, creating a refresh token");
            }
        }
        PrivateKey privateKey = getPrivateKey();

        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(currentUser)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5L, ChronoUnit.MINUTES)))
                .signWith(privateKey)
                .compact();
    }


    private static Key getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String publicKeyContent = FileUtils.readFromFile(PUBLIC_KEY_PATH);
        publicKeyContent = publicKeyContent.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace(
                "-----END PUBLIC KEY-----", "");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String privateKeyContent = FileUtils.readFromFile(PRIVATE_KEY_PATH);
        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
}

