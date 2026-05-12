package com.example.jablog.config.security

import com.example.jablog.service.CustomUserDetailsService
import com.example.jablog.service.SecurityAccessService
import lombok.RequiredArgsConstructor
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class PosterAuthorizationManager(private val securityAccessService: SecurityAccessService,
                                      private val customUserDetailsService: CustomUserDetailsService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        auth: Supplier<out Authentication?>,
        context: RequestAuthorizationContext
    ): AuthorizationResult {

        val session = context.request.getSession(false)

        val boardName = context.variables["boardName"] as String
        val isThread = !(context.variables["thread"]).isNullOrBlank()

        val user = (session.getAttribute(boardName) ?: customUserDetailsService.createDefault())
                as CustomUserDetails

        val canAccess = securityAccessService.canAccess(
            boardName,
            user,
            "POST",
            isThread,
            false
        )

        return AuthorizationDecision(canAccess)
    }
}
