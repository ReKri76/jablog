package io.rekri.jablog.config.security.authorizations

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.PosterAccessService
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class PosterAuthorizationManager(private val posterAccessService: PosterAccessService,
                                 private val customUserDetailsService: CustomUserDetailsService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        auth: Supplier<out Authentication?>,
        context: RequestAuthorizationContext
    ): AuthorizationResult {

        val session = context.request.getSession(false)

        val boardName = context.variables["boardName"] as String
        val threadId : String? = context.variables["thread"]
        val sessionAttr = session?.getAttribute(boardName)

        val user = (sessionAttr ?: customUserDetailsService.createDefault()) as CustomUserDetails

        val canAccess = posterAccessService.canAccess(
            data = SecurityData.Poster(
                boardName = boardName,
                user = user,
                threadId = threadId

            )
        )

        return AuthorizationDecision(canAccess)
    }
}
