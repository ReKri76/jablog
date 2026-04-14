package com.example.jablog.service;

import com.example.jablog.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SecurityAccessService {

    public boolean canAccess(String boardName, @NonNull CustomUserDetails user, String method, boolean isThread,
                             boolean isUser){

        if (!Objects.equals(boardName, user.getBoardName()))
            return false;

        if (isUser)
            return user.getRole().equals("ROLE_ADMIN") && user.getBoardRules()[2].equals("d");

        String[] rules = user.getBoardRules();
        String[] currentRules = new String[4];

        int l = switch (user.getRole()) {
            case "ROLE_ADMIN" -> 0;
            case "ROLE_GROUP" -> 1;
            default -> 2;
        };

        System.arraycopy(rules, l, currentRules, 0, currentRules.length);

        boolean hasAccess = isThread == currentRules[3].equals("x");

        return switch (method) {
            case "POST" -> currentRules[1].equals("w") && hasAccess;
            case "DELETE" -> currentRules[2].equals("d") && hasAccess;
            case "GET" -> currentRules[0].equals("r") && hasAccess;
            default -> false;
        };
    }
}
