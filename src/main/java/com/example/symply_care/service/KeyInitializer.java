package com.example.symply_care.service;


import com.example.symply_care.repository.RSAKeysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyInitializer implements InitializingBean {

    private final KeyRotationService keyRotationService;
    private final RSAKeysRepository RSAKeysRepository;

    //פונה לפעולה שיוצרת מפתחות במידה ואין
    @Override
    public void afterPropertiesSet() {
        if (RSAKeysRepository.count() == 0) {
            keyRotationService.rotateKeys();
        }
    }
}
