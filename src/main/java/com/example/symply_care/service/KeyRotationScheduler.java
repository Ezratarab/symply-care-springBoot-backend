package com.example.symply_care.service;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for scheduling the key rotation.
 * It is scheduled to run every 6 months, on the first day of the month.
 * It will call the KeyRotationService to rotate the keys.
 */


@Service
public class KeyRotationScheduler {

    private final KeyRotationService keyRotationService;

    public KeyRotationScheduler(KeyRotationService keyRotationService) {
        this.keyRotationService = keyRotationService;
    }

    // TODO 4: KeyRotationScheduler class, Implement Key Rotation Scheduler (RSA)


    @Scheduled(cron = "0 0 0 1 1/6 *")
    //  For example, to rotate keys at 00:00 AM on the first day of every 6th month,
    //  you would use the following cron expression: 0 0 0 1 1/6 * *
    /*
    This cron expression breaks down as follows:
        0: At the 0th second.
        0: At the 0th minute.
        0: At the 0th hour (midnight).
        1: On the first day of the month.
        1/6: Every 6th month starting from January.
        * : Any day of the week.
     */
    public void rotateKeys() {
        System.out.println("rotateKeys by scheduler");
        keyRotationService.rotateKeys();
    }
}
