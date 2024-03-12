package com.example.symply_care.service;

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


@Service
@RequiredArgsConstructor
public class KeyRotationService {

    private final JwtUtil jwtUtil;
    private final com.example.symply_care.repository.RSAKeysRepository RSAKeysRepository;
    private final com.example.symply_care.util.RSAKeyGenerator RSAKeyGenerator;

    @Transactional
    public void rotateKeys() {
        try {
            KeyPair newKeyPair = RSAKeyGenerator.generateKeyPair();

            System.out.println("New private key: " + newKeyPair.getPrivate());
            System.out.println("New public key: " + newKeyPair.getPublic());

            Optional<RSAKeysEntity> currentKeysOptional = RSAKeysRepository.findById(1);
            if (currentKeysOptional.isPresent()) {
                RSAKeysEntity currentRSAKeysEntity = currentKeysOptional.get();

                System.out.println("Current keys before rotation - Private: " +
                        currentRSAKeysEntity.getPrivate_key() + ", Public: " +
                        currentRSAKeysEntity.getPublic_key());

                RSAKeysEntity oldRSAKeysEntity = new RSAKeysEntity(2,
                        currentRSAKeysEntity.getPrivate_key(),
                        currentRSAKeysEntity.getPublic_key());
                RSAKeysRepository.save(oldRSAKeysEntity);
                RSAKeysRepository.flush();

                currentRSAKeysEntity.setPrivate_key(newKeyPair.getPrivate().getEncoded());
                currentRSAKeysEntity.setPublic_key(newKeyPair.getPublic().getEncoded());
                RSAKeysRepository.save(currentRSAKeysEntity);

                System.out.println("New keys after rotation - Private: " +
                        newKeyPair.getPrivate().getEncoded() + ", Public: " +
                        newKeyPair.getPublic().getEncoded());

                if (jwtUtil.fetchTheCurrentKeyFormDatabase())
                    System.out.println("Key fetched from database");
                else {
                    throw new RuntimeException("No RSA keys found in the database");
                }

            } else {
                RSAKeysEntity newRSAKeysEntity = new RSAKeysEntity(1,
                        newKeyPair.getPrivate().getEncoded(),
                        newKeyPair.getPublic().getEncoded());
                RSAKeysRepository.save(newRSAKeysEntity);
            }
        } catch (NoSuchAlgorithmException e) {
        }
    }
}

