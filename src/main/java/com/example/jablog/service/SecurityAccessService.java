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
        int l;

        switch (user.getRole()) {
            case "ROLE_ADMIN":
                l = 0;
                break;
            case "ROLE_GROUP":
                l=1;
                break;
            default:
                l=2;
                break;
        }

        currentRules[0]=rules[0+l];
        currentRules[1]=rules[1+l];
        currentRules[2]=rules[2+l];
        currentRules[3]=rules[3+l];

        boolean hasAccess = isThread == currentRules[3].equals("x");

        switch (method){
            case"POST":
                return currentRules[1].equals("w") && hasAccess;
            case "DELETE":
                return currentRules[2].equals("d") && hasAccess;
            case "GET":
                return currentRules[0].equals("r") && hasAccess;
            default:
                return false;
        }
    }
}
