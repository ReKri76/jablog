package com.example.jablog.config.security;

import com.example.jablog.config.CustomUserDetails;
import com.example.jablog.service.SecurityAccessService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final SecurityAccessService securityAccessService;

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> auth,
                                                   RequestAuthorizationContext context) {

        assert context != null;

        String boardName = context.getVariables().get("boardName");
        HttpServletRequest req = context.getRequest();

        boolean canAccess = securityAccessService.canAccess(boardName,
                (CustomUserDetails) Objects.requireNonNull(auth.get()).getPrincipal(), req.getMethod());

        return new AuthorizationDecision(canAccess);
    }
}
