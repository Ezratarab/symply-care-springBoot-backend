package com.example.symply_care.service;


import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service //בתכלס אפשר להחליף בין זה לcomponent רק שבעתיד spring יכולים להוסיף לזה פיצ'רים מיוחדים
@EnableScheduling
public class KeyRotationScheduler {

    private final KeyRotationService keyRotationService;

        public KeyRotationScheduler(KeyRotationService keyRotationService) {
            this.keyRotationService = keyRotationService;
        }

        //ישנה את המפתחות כל הראשון לחודש כל 3 חודשים
        @Scheduled(cron = "0 0 0 1 1/4 *")
        public void rotateKeys() {
            System.out.println("rotateKeys by scheduler");
            keyRotationService.rotateKeys();
        }
}
