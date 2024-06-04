package com.example.symply_care.util;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.symply_care.dto.AuthenticationRequest;
import com.example.symply_care.entity.RSAKeysEntity;
import com.example.symply_care.entity.Role;
import com.example.symply_care.entity.User;
import com.example.symply_care.entity.Users;
import com.example.symply_care.exceptions.TokenValidationException;
import com.example.symply_care.repository.RSAKeysRepository;
import com.example.symply_care.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component //על מנת ליצור עצם של מחלקה!! מבלי תוספת קוד
@RequiredArgsConstructor
public class JwtUtil {

    private final RSAKeysRepository RSAKeysRepository;

    private final KeyPair keyPair;

    private PrivateKey privateKey;
    private PublicKey publicKey;



    private long TOKEN_EXPIRATION_TIME = JwtProperties.EXPIRATION_TIME;

    private final UsersRepository usersRepository;
    private String createToken(Claims claims, Users user) {
        claims.put("roles", user.getRoles().stream().map(Role::getRole).collect(Collectors.toList()));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }
    public String generateToken(AuthenticationRequest authenticationRequest, UserDetails userDetails) {

        if (fetchTheCurrentKeyFormDatabase())
            System.out.println("Key fetched from database");
        else {
            throw new RuntimeException("No RSA keys found in the database");
        }

        System.out.println("privateKey: " + privateKey);
        System.out.println("publicKey: " + keyPair.getPublic());

        Optional<Users> user = usersRepository.findByEmail(authenticationRequest.getEmail());
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        return createToken(claims, user.get());

    }
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new RuntimeException("The token signature is invalid: " + e.getMessage());
        }
    }


    private PrivateKey convertBytesToPrivateKey(byte[] keyBytes) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to convert byte array to PrivateKey", e);
        }
    }

    private PublicKey convertBytesToPublicKey(byte[] keyBytes) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to convert byte array to PublicKey", e);
        }
    }

    //פועל בעת פעולת הrefresh. פעולה זו יוצרת token
    public String generateTokenFromUsername(String email) {
        Optional<Users> user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        Claims claims = Jwts.claims().setSubject(email);
        return createToken(claims, user.get());
    }
    public String extractUsername(String token) {
        DecodedJWT jwt = JWT.decode(token);

        String sub = jwt.getClaim("sub").asString();
        return sub;
    }

    private <T> T extractClaim(String string, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(string);
        return claimsResolver.apply(claims);
    }

    public boolean fetchTheCurrentKeyFormDatabase() {
        Optional<RSAKeysEntity> currentRsaOptional = RSAKeysRepository.findById(1);
        if (currentRsaOptional.isPresent()) {
            RSAKeysEntity currentRsaKeysEntity = currentRsaOptional.get();
            privateKey = convertBytesToPrivateKey(currentRsaKeysEntity.getPrivate_key());
            publicKey = convertBytesToPublicKey(currentRsaKeysEntity.getPublic_key());
            System.out.println("publicKey: " + publicKey);
            System.out.println("privateKey: " + privateKey);
            return true;
        }
        return false;
    }

    public Claims extractAllClaims(String token) {
        PublicKey currentKey = getCurrentPublicKey();
        try {
            return parseToken(token, currentKey, false);
        } catch (ExpiredJwtException e) {
            if (isTokenWithinGracePeriod(e, JwtProperties.IDLE_TIME_FOR_REFRESH_TOKEN)) {
                return e.getClaims();
            }
        } catch (Exception e) {
        }
        PublicKey oldKey = getOldPublicKey();
        try {
            return parseToken(token, oldKey, true);
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("Token expired beyond the grace period", e);
        } catch (Exception e) {
            throw new TokenValidationException("Token validation failed with both current and old keys", e);
        }
    }

    private Claims parseToken(String token, PublicKey publicKey, boolean considerGracePeriod) throws ExpiredJwtException {
        long clockSkewMillis = considerGracePeriod ? JwtProperties.IDLE_TIME_FOR_REFRESH_TOKEN : 0;
//במידה והמפתח הציבורי תקף, נצפה שהאסימון יהיה אמין לשניית הבדיקה ללא שינוי השעון, אך אם האסימון ישן מעט ובזמן רענון האסימון התחלפו מפתחות הRSA אז כביכול נזיז את השעון לאחור על מנת שיהיה אפשרי לבדוק
        return Jwts.parser()
                .setSigningKey(publicKey)
                .setAllowedClockSkewSeconds(clockSkewMillis / 1000)
                .parseClaimsJws(token)
                .getBody();
    }

    //בודק אם הtoken פג בתוך זמן הrefresh
    private boolean isTokenWithinGracePeriod(ExpiredJwtException e, long gracePeriodMillis) {
        Date expiration = e.getClaims().getExpiration();
        long currentTimeMillis = System.currentTimeMillis();
        long expiredDurationMillis = currentTimeMillis - expiration.getTime();
        return expiredDurationMillis <= gracePeriodMillis;
    }

    private PublicKey getCurrentPublicKey() {
        Optional<RSAKeysEntity> currentRsaOptional = RSAKeysRepository.findById(1);
        if (currentRsaOptional.isPresent()) {
            RSAKeysEntity currentRsaKeysEntity = currentRsaOptional.get();
            publicKey = convertBytesToPublicKey(currentRsaKeysEntity.getPublic_key());
            System.out.println("publicKey: " + publicKey);
            return publicKey;
        } else {
            throw new RuntimeException("No RSA keys found in the database");
        }
    }

    private PublicKey getOldPublicKey() {
        Optional<RSAKeysEntity> currentRsaOptional = RSAKeysRepository.findById(2);
        if (currentRsaOptional.isPresent()) {
            RSAKeysEntity currentRsaKeysEntity = currentRsaOptional.get();
            publicKey = convertBytesToPublicKey(currentRsaKeysEntity.getPublic_key());
            return publicKey;
        } else {
            throw new RuntimeException("No Old RSA keys found in the database");
        }
    }


    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
