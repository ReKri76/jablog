package com.example.jablog.config.security;

import com.example.jablog.service.CustomUserDetailsService;
import com.example.jablog.service.SecurityAccessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final SecurityAccessService securityAccessService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public @Nullable AuthorizationResult authorize(@NonNull Supplier<? extends @Nullable Authentication> auth,
                                                   @NonNull RequestAuthorizationContext context) {

        HttpSession session = context.getRequest().getSession(false);

        String boardName = context.getVariables().get("boardName");
        String thread = context.getVariables().get("thread");
        String post = context.getVariables().get("post");
        HttpServletRequest req = context.getRequest();
        String user = req.getRequestURI().split("/")[1];

        CustomUserDetails customUserDetails = (CustomUserDetails) session.getAttribute(boardName);

        if (customUserDetails == null)
            customUserDetails = customUserDetailsService.createDefault();

        boolean canAccess = securityAccessService.canAccess(
                boardName,
                customUserDetails,
                req.getMethod(),
                ((thread != null) != (post == null)),
                user.equals("user")
                );

        return new AuthorizationDecision(canAccess);
    }
}
