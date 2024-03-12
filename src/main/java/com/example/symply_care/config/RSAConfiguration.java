package com.example.symply_care.config;

import com.example.symply_care.util.RSAKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RSAConfiguration {

    //יוצר 2 מפתחות RSA: הראשון ציבורי על מנת לאמת את הtoken והשני פרטי על מנת לחתום את האסימון

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

