package com.example.symply_care.service;


import com.example.symply_care.repository.RSAKeysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for initializing the RSA keys at startup.
 * If the database does not contain any keys, it will generate a new pair and store them.
 * If the database already contains keys, it will do nothing.
 */



// TODO 3: RSA KeyInitializer class,  Implement Key Rotation Logic, at first startup

@Service
@RequiredArgsConstructor
public class KeyInitializer implements InitializingBean {

    private final KeyRotationService keyRotationService;
    private final RSAKeysRepository RSAKeysRepository;



    @Override
    public void afterPropertiesSet() {
        // Check if the database already has keys. If not, generate and store them.
        if (RSAKeysRepository.count() == 0) {
            keyRotationService.rotateKeys();
        }
    }
}
