package io.rekri.jablog.config.security.authorizations

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.UsersAccessService
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class UsersAuthorizationManager(private val usersAccessService: UsersAccessService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        auth: Supplier<out Authentication?>,
        context: RequestAuthorizationContext
    ): AuthorizationResult {
        val session = context.request.getSession(false)

        val boardName = context.variables["boardName"] as String

        val user = session.getAttribute(boardName) as? CustomUserDetails ?:
            return AuthorizationDecision(false)

        val canAccess = usersAccessService.canAccess(
            data = SecurityData.Users(
                boardName = boardName,
                user = user
            )
        )

        return AuthorizationDecision(canAccess)
    }
}
