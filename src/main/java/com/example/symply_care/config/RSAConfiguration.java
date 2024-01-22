package com.example.symply_care.config;


// TODO 3: RSAConfiguration class

import com.example.symply_care.util.RSAKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to generate a key pair for RSA algorithm, by using the RSAKeyGenerator class.
 * The key pair is used to sign and verify the JWT token.
 * The private key is used to sign the JWT token, and the public key is used to verify the JWT token.
 * The private key stored in the database, and the public key sent to the client.
 * The client uses the public key to verify the JWT token.
 * The public key sent to the client in response to the login request.
 * The private key is stored in the database in the RSAKeysEntity table.
 *
 */
@Configuration
public class RSAConfiguration {

    @Bean
    public KeyPair rsaKeyPair() {
        try {
            RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
            return rsaKeyGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }
}

