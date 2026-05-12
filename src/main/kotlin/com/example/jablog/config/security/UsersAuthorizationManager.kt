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
class UsersAuthorizationManager(private val securityAccessService: SecurityAccessService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        auth: Supplier<out Authentication?>,
        context: RequestAuthorizationContext
    ): AuthorizationResult {
        val session = context.request.getSession(false)

        val boardName = context.variables["boardName"] as String

        val user = session.getAttribute(boardName) as? CustomUserDetails ?:
            return AuthorizationDecision(false)

        val canAccess = securityAccessService.canAccess(
            boardName,
            user,
            context.request.requestURI,
            false,
            true
        )

        return AuthorizationDecision(canAccess)
    }
}
