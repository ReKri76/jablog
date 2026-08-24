package io.rekri.jablog.config.security.authorizations

import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.GetterAccessService
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

@Component
class GetterAuthorizationManager(private val getterAccessService: GetterAccessService) :
    AuthorizationManager<RequestAuthorizationContext> {

    override fun authorize(
        auth: Supplier<out Authentication?>,
        context: RequestAuthorizationContext
    ): AuthorizationResult {


        val boardName = context.variables["boardName"] as String
        val threadId : String? = context.variables["thread"]

        val user = auth.get().principal as String?

        val canAccess = getterAccessService.canAccess(
            data = SecurityData.Getter(
                boardName = boardName,
                user = user,
                threadId = threadId
            )
        )

        return AuthorizationDecision(canAccess)
    }
}