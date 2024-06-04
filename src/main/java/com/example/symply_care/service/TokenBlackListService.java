package com.example.symply_care.service;


import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlackListService {
    //TODO ConcurrentHashMap להוסיף למצגת על
    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();
    public void addToBlacklist(String token) {
        blacklist.put(token, Instant.now().plusMillis(10000));//10 שניות
        /*
        הוספת 10 שניות לזמן כדי למנוע מתקפת brute force כלומר שתוקף לא יוכל להתחבר עם אותו משתמש ב10 שניות בהן התחבר וכך כדי למנוע מהמערכת קריסה
         */
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
