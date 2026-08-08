package io.rekri.jablog.config.security

import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.DeleterAccessService
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class DeleterAuthorizationManager(private val deleterAccessService: DeleterAccessService,
                                  private val customUserDetailsService: CustomUserDetailsService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(auth: Supplier<out Authentication?>, context: RequestAuthorizationContext):
            AuthorizationResult {

        val session = context.request.getSession(false)

        val boardName = context.variables["boardName"] as String
        val postId = context.variables["post"] //может прийти запрос как на удаление треда, таки на удаление поста
        val sessionAttr = session?.getAttribute(boardName)

        val user = (sessionAttr ?: customUserDetailsService.createDefault()) as CustomUserDetails

        val canAccess = deleterAccessService.canAccess(
            data = SecurityData.Deleter(
                boardName = boardName,
                user = user,
                postId = postId
            )
        )

        return AuthorizationDecision(canAccess)
    }
}