package com.example.symply_care.service;


/**
 * KeyRotationService class, Implement Key Rotation Logic
 * This class is responsible for rotating the RSA keys.
 * It will be called by the KeyRotationScheduler.
 * It will generate a new key pair and store it in the database.
 * It will also update the kid in the JWTUtil class.
 */


// TODO 3: RSA KeyRotationService class,  Implement Key Rotation Logic

import com.example.symply_care.entity.RSAKeysEntity;
import com.example.symply_care.repository.RSAKeysRepository;
import com.example.symply_care.util.JwtUtil;
import com.example.symply_care.util.RSAKeyGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 Key rotation is the process of replacing old keys with new keys.
 It is a security best practice to rotate keys periodically.
 It is also a security best practice to have multiple keys in the system, so that you can rotate keys without affecting the system.
 In this tutorial, we will implement key rotation by having two keys in the system.
 We will have a current key and an old key.
 When we rotate the keys, we will create a new key and make it the current key, and the current key will become the old key.
 it uses rsaKeyGenerator to generate the key pair.
 and it uses RSAKeysRepository to store the keys in the database.
 It also uses jwtUtil to update the kid in the JWTUtil class.
 */
@Service
@RequiredArgsConstructor
public class KeyRotationService {

    private final JwtUtil jwtUtil;
    private final com.example.symply_care.repository.RSAKeysRepository RSAKeysRepository; // Assume this is your way to store private keys
    private final com.example.symply_care.util.RSAKeyGenerator RSAKeyGenerator;

    @Transactional
    public void rotateKeys() {
        try {
            // Generate a new key pair
            KeyPair newKeyPair = RSAKeyGenerator.generateKeyPair();

            // log the new keys, for debugging purposes
            System.out.println("New private key: " + newKeyPair.getPrivate());
            System.out.println("New public key: " + newKeyPair.getPublic());

            // Check if the database already has keys. If yes, rotate the keys.
            Optional<RSAKeysEntity> currentKeysOptional = RSAKeysRepository.findById(1);
            if (currentKeysOptional.isPresent()) {
                RSAKeysEntity currentRSAKeysEntity = currentKeysOptional.get();

                // Debug: Print current keys before rotation
                System.out.println("Current keys before rotation - Private: " +
                        currentRSAKeysEntity.getPrivate_key() + ", Public: " +
                        currentRSAKeysEntity.getPublic_key());

                // Create as a new key (kid 2) with the old key data
                RSAKeysEntity oldRSAKeysEntity = new RSAKeysEntity(2,
                        currentRSAKeysEntity.getPrivate_key(),
                        currentRSAKeysEntity.getPublic_key());
                // Save the new key (kid 2) in the database
                RSAKeysRepository.save(oldRSAKeysEntity);
                RSAKeysRepository.flush();

                // Update the existing key (kid 1) with the new key data
                currentRSAKeysEntity.setPrivate_key(newKeyPair.getPrivate().getEncoded());
                currentRSAKeysEntity.setPublic_key(newKeyPair.getPublic().getEncoded());
                // Save the updated key (kid 1) in the database
                RSAKeysRepository.save(currentRSAKeysEntity);

                // Debug: Print keys after rotation
                System.out.println("New keys after rotation - Private: " +
                        newKeyPair.getPrivate().getEncoded() + ", Public: " +
                        newKeyPair.getPublic().getEncoded());

                // Update the new kid in the JWTUtil class
                if (jwtUtil.fetchTheCurrentKeyFormDatabase())
                    System.out.println("Key fetched from database");
                else {
                    throw new RuntimeException("No RSA keys found in the database");
                }

            } else {
                // if the database does not have any keys, create a new key pair and store it in the database
                RSAKeysEntity newRSAKeysEntity = new RSAKeysEntity(1,
                        newKeyPair.getPrivate().getEncoded(),
                        newKeyPair.getPublic().getEncoded());
                RSAKeysRepository.save(newRSAKeysEntity);
            }
        } catch (NoSuchAlgorithmException e) {
            // Handle exception
        }
    }
}

