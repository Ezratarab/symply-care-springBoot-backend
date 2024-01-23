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

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RSAKeysRepository RSAKeysRepository; // Store private keys

    // TODO RSA
    private final KeyPair keyPair; // Injected

    private PrivateKey privateKey;
    private PublicKey publicKey;

    // TODO Update JwtUtil to Support Key Rotation
    /*
    Integrating the JWT token handling, with graceful key transition, into
    a system  - requires a few steps:
    The main goal is to ensure that JWT tokens (issued for user authentication)
    can be verified using either the current or the old RSA keys during the key rotation period.
     */

    private long TOKEN_EXPIRATION_TIME = JwtProperties.EXPIRATION_TIME;

    private final UsersRepository usersRepository;

    public boolean fetchTheCurrentKeyFormDatabase() {
        // Fetch the current private key as a byte array, it is stored in the database as a key id 1
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

    // Generate a JWT token for a user, first time login
    public String generateToken(AuthenticationRequest authenticationRequest, UserDetails userDetails) {

        if (fetchTheCurrentKeyFormDatabase())
            System.out.println("Key fetched from database");
        else {
            throw new RuntimeException("No RSA keys found in the database");
        }

        // display the private key
        System.out.println("privateKey: " + privateKey);
        // display the public key
        System.out.println("publicKey: " + keyPair.getPublic());

        Optional<Users> user = usersRepository.findByEmail(authenticationRequest.getEmail());
        //TODO: to check if this is okay with Eli

        // create claims and set the subject (username)
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        // crete token string by adding the user's roles to the claims
        return createToken(claims, user.get());

    }

    private PrivateKey convertBytesToPrivateKey(byte[] keyBytes) {
        // TODO RSA - Convert a byte array to PrivateKey
        try {
            // PKCS8EncodedKeySpec: This class represents the DER encoding of a private key, according to the format
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            // KeyFactory: This class is used to convert a byte array to a PrivateKey object
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to convert byte array to PrivateKey", e);
        }
    }

    private PublicKey convertBytesToPublicKey(byte[] keyBytes) {
        try {
            // X509EncodedKeySpec: This class represents the DER encoding of a public key, according to the format
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            // KeyFactory: This class is used to convert a byte array to a PublicKey object
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to convert byte array to PublicKey", e);
        }
    }


    // helper method to create a JWT token, used by generateToken() and generateTokenFromUsername() methods
    // TODO RSA : SignatureAlgorithm: The cryptographic algorithm used to sign the JWT.
    private String createToken(Claims claims, Users user) {
        claims.put("roles", user.getRoles().stream().map(Role::getRole).collect(Collectors.toList()));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.RS256, privateKey) // Use RS256 with an RSA private key
                .compact();
    }


    // Generate a JWT token from a username, when refreshing the token
    public String generateTokenFromUsername(String email) {
        // fetch the user details from the database using the email
        Optional<Users> user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // create claims and set the subject (username)
        Claims claims = Jwts.claims().setSubject(email);
        // crete token string by adding the user's roles to the claims
        return createToken(claims, user.get());
    }


    // Extract the expiration date from a JWT token, and implicitly validate the token
    // This implementation implicitly validates the signature when extracting claims:
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // extract the username from the JWT token
            String username = extractUsername(token);
            // If signature verification fails, extractUsername will throw an exception.

            // check if the username extracted from the JWT token matches the username in the UserDetails object
            // and the token is not expired
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (io.jsonwebtoken.SignatureException e) {
            // Handle the invalid signature here
            throw new RuntimeException("The token signature is invalid: " + e.getMessage());
        }
        // Other exceptions related to token parsing can also be caught here if necessary
    }

    // Extract the username from a JWT token
    public String extractUsername(String token) {
        // TODO 0, fix error here, extractAllClaims(token) returns null
        // Decode the JWT without verification (note: this doesn't validate the token!)
        // TODO 0, fix in pom.xml adding <artifactId>java-jwt</artifactId>, see pom.xml
        DecodedJWT jwt = JWT.decode(token);

        // sub (Subject): This claim identifies the subject of the token (usually the user).
        String sub = jwt.getClaim("sub").asString();

        /* Here some info about JWT claims:
        JSON Web Tokens (JWTs) are a compact, URL-safe way of representing claims to be transferred between two parties.
        The claims in a JWT are encoded as a JSON object.
        Claims can be broadly categorized into three types: Registered, Public, and Private claims.

            Registered Claims: These are predefined claims that are not mandatory but recommended,
            to provide a set of useful, interoperable claims. Some of them are:

            iss (Issuer): This claim identifies the issuer of the token.
            sub (Subject): This claim identifies the subject of the token (usually the user).
            aud (Audience): This claim identifies the recipients that the JWT is intended for.
            exp (Expiration Time): Specifies the date/time after which the JWT is no longer valid.
            nbf (Not Before): Specify the date/time before which the JWT must not be accepted for processing.
            iat (Issued At): Specifies the date/time at which the JWT was issued.
            jti (JWT ID): This is a unique identifier for the JWT, and can be used to prevent the JWT from being replayed.

            Public Claims: These are claims that can be defined at will.
            They should be defined in the IANA JSON Web Token Claims registry or be defined as a URI that contains a
            collision-resistant namespace. Examples include:

            name: The subject's full name.
            given_name: The subject's given name or first name.
            family_name: The subject's family name or last name.
            middle_name: The subject's middle name.
            nickname: The subject's nickname.
            preferred_username: The subject's preferred username.
            profile: The subject's profile page URL.
            picture: The subject's profile picture URL.
            website: The subject's personal website URL.
            email: The subject's preferred email address.
            gender: The subject's gender.
            And many more...

            Private Claims:
            These are claims that are used to convey information about the token that is pertinent
            to the parties involved. They are neither registered nor public claims.
            Examples could include user roles, permissions, or any other custom data specific to the application's needs.

            While using JWT, it's essential to avoid placing sensitive or private information in the claims,
            especially if the JWT is not encrypted, as it can be easily decoded by anyone who has access to the token.

         */
        return sub;
    }

    private <T> T extractClaim(String string, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(string);
        return claimsResolver.apply(claims);
    }

    // TODO RSA - Extract all claims from a JWT token, using the PUBLIC key: This implicitly validates the signature
    /*
    This code is designed to validate JWTs using RSA public keys.
    It first tries to validate the token with the current public key,
    and if it fails due to token expiration or a signature mismatch,
    it then tries with an old public key.
    This is a common strategy in systems where key rotation is practiced for enhanced security.
    The methods handle scenarios like token expiration (including a grace period) and key rotation effectively.
     */

    public Claims extractAllClaims(String token) {
        // First, try with the current public key
        PublicKey currentKey = getCurrentPublicKey();
        try {
            return parseToken(token, currentKey, false);
        } catch (ExpiredJwtException e) {
            // If expired, check if it's within the grace period
            if (isTokenWithinGracePeriod(e, JwtProperties.IDLE_TIME_FOR_REFRESH_TOKEN)) {
                return e.getClaims(); // Token is expired but within the grace period
            }
            // If not within grace period, continue to try with the old key
        } catch (Exception e) {
            // If signature mismatch, continue to try with the old key
        }

        // Now, try with the old public key
        PublicKey oldKey = getOldPublicKey();
        try {
            return parseToken(token, oldKey, true); // Assume a grace period already considered
        } catch (ExpiredJwtException e) {
            // If still expired, then it's beyond the grace period
            throw new TokenValidationException("Token expired beyond the grace period", e);
        } catch (Exception e) {
            throw new TokenValidationException("Token validation failed with both current and old keys", e);
        }
    }

    private Claims parseToken(String token, PublicKey publicKey, boolean considerGracePeriod) throws ExpiredJwtException {
        // Adjust the clock skew (grace period) based on the boolean flag.
        long clockSkewMillis = considerGracePeriod ? JwtProperties.IDLE_TIME_FOR_REFRESH_TOKEN : 0;

        // Parse the JWT and return the claims. The clock skew allows for some leeway in token expiration.
        return Jwts.parser()
                .setSigningKey(publicKey)
                .setAllowedClockSkewSeconds(clockSkewMillis / 1000) // Convert milliseconds to seconds
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenWithinGracePeriod(ExpiredJwtException e, long gracePeriodMillis) {
        // Get the expiration time of the token from the exception.
        Date expiration = e.getClaims().getExpiration();
        // Get the current time.
        long currentTimeMillis = System.currentTimeMillis();
        // Calculate how long it has been since the token expired.
        long expiredDurationMillis = currentTimeMillis - expiration.getTime();
        // Return true if the token expired within the grace period; false otherwise.
        return expiredDurationMillis <= gracePeriodMillis;
    }

    private PublicKey getCurrentPublicKey() {
        // Retrieve the current public key from the database or key store.
        // Example: This could be the key with the latest 'kid' or a 'current' flag.
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
        // Retrieve the old public key from the database or key store.
        // Example: This could be the key with the second latest 'kid' or a specific flag.
        Optional<RSAKeysEntity> currentRsaOptional = RSAKeysRepository.findById(2);
        if (currentRsaOptional.isPresent()) {
            RSAKeysEntity currentRsaKeysEntity = currentRsaOptional.get();
            publicKey = convertBytesToPublicKey(currentRsaKeysEntity.getPublic_key());
            return publicKey;
        } else {
            throw new RuntimeException("No Old RSA keys found in the database");
        }
    }


    // Check if a JWT token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    // Extract the expiration date from a JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
