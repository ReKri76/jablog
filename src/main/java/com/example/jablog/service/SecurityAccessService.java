package com.example.jablog.service;

import com.example.jablog.config.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SecurityAccessService {

    @Transactional
    public boolean canAccess(String boardName, CustomUserDetails user, String method){

        if (!Objects.equals(boardName, user.getBoardName()))
            return false;

        String[] rules = user.getBoardRules();
        String[] currentRules = new String[4];
        int l;

        switch (user.getRole()) {
            case "ROLE_ADMIN":
                l = 0;
            case "ROLE_GROUP":
                l=1;
            default:
                l=2;
        }

        currentRules[0]=rules[0+l];
        currentRules[1]=rules[1+l];
        currentRules[2]=rules[2+l];
        currentRules[3]=rules[3+l];

        switch (method){
            case"POST":
                return currentRules[1].equals("w");
            case "DELETE":
                return currentRules[2].equals("d");
            case "GET":
                return currentRules[0].equals("r");
            default:
                return false;
        }
    }
}
