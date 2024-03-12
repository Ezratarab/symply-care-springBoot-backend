package com.example.symply_care.service;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeyRotationScheduler {

    private final KeyRotationService keyRotationService;

        public KeyRotationScheduler(KeyRotationService keyRotationService) {
            this.keyRotationService = keyRotationService;
        }

        //ישנה את המפתחות כל הראשון לחודש כל 4 חודשים
        @Scheduled(cron = "0 0 0 1 1/4 *")
        public void rotateKeys() {
            System.out.println("rotateKeys by scheduler");
            keyRotationService.rotateKeys();
        }
}
