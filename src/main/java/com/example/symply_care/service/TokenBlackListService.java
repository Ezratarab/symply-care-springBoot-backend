package com.example.symply_care.service;


import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlackListService {
    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();
    public void addToBlacklist(String token) {
        blacklist.put(token, Instant.now().plusMillis(10000));
        System.out.println("Token added to blacklist: " + token);
        removeExpiredTokens();
    }

    private void removeExpiredTokens() {
        for (String token : blacklist.keySet()) {
            if (Instant.now().isAfter(blacklist.get(token))) {
                blacklist.remove(token);
            }
        }
    }

    public boolean isBlacklisted(String token) {
        Instant expiration = blacklist.get(token);
        if (expiration == null) {
            return false;
        }
        if (Instant.now().isAfter(expiration)) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
