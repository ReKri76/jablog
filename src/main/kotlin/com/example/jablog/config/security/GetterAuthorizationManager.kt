package com.example.jablog.config.security

import com.example.jablog.service.CustomUserDetailsService
import com.example.jablog.service.SecurityAccessService
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class GetterAuthorizationManager(private val securityAccessService: SecurityAccessService,
                                private val customUserDetailsService: CustomUserDetailsService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        auth: Supplier<out Authentication?>,
        context: RequestAuthorizationContext
    ): AuthorizationResult {
        val session = context.request.getSession(false)

        val boardName = context.variables["boardName"]
        val isThread = !(context.variables["post"]).isNullOrBlank()

        val user = (session.getAttribute(boardName) ?: customUserDetailsService.createDefault())
                as CustomUserDetails

        val canAccess = securityAccessService.canAccess(
            boardName,
            user,
            "GET",
            isThread,
            false
        )

        return AuthorizationDecision(canAccess)
    }
}