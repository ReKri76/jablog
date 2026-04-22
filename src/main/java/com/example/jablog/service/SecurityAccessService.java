package com.example.jablog.service;

import com.example.jablog.config.security.CustomUserDetails;
import com.example.jablog.repository.SecurityRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SecurityAccessService {

    private final SecurityRepository securityRepository;

    public boolean canAccess(String boardName, @NonNull CustomUserDetails user, String method, boolean isThread,
                             boolean isUser){

        if (user.getBoardName().equals("ANON")){
            user.setBoardName(boardName);
            user.setBoardRules(securityRepository.getRulesByBoardName(boardName));
        }

        if (!Objects.equals(boardName, user.getBoardName()))
            user.setRole("ROLE_ANON");

        if (isUser)
            return user.getRole().equals("ROLE_ADMIN");

        final String rules = user.getBoardRules();

        final int shift = PosterService.SIZE_OF_GROUP * switch (user.getRole()) {
            case "ROLE_ADMIN" -> 0;
            case "ROLE_GROUP" -> 1;
            default -> 2;
        };

        if (rules == null || rules.length() != PosterService.SIZE_OF_ARRAY_OF_RULES)
            return false;

        final String currentRules = rules.substring(shift, shift + PosterService.SIZE_OF_GROUP);

        if (currentRules.charAt(0) != 'r')
            return false;

        return switch (method) {
            case "POST" ->
                currentRules.charAt(1) == 'w' && (isThread || currentRules.charAt(3) == 'x');

            case "DELETE" ->
                currentRules.charAt(2) == 'd' && (!isThread || currentRules.charAt(3) == 'x');

            case "GET" ->
                !isThread || !anyOtherFlagsIsEmpty(currentRules);

            default -> false;
        };
    }

    private boolean anyOtherFlagsIsEmpty(String rules){
        for (int i = 1; i < rules.length(); i++){
            if (rules.charAt(i) != '-')
                return false;
        }
        return true;
    }
}
