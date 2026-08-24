package io.rekri.jablog.service.security

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
open class DeleterAccessService(private val securityRepository: SecurityRepository,
    customUserDetailsService: CustomUserDetailsService) : XAccessService(securityRepository, customUserDetailsService) {

    override fun canAccess(data: SecurityData): Boolean {
        return when(data){
            is SecurityData.Deleter -> {

                data.postId?.let{
                    val postId = it.toLong()
                    if (!securityRepository.isPostInBoard(data.boardName, postId))
                        return false
                }

                val isPost = !data.postId.isNullOrBlank()

                val user = loadUserByAccountNameAndBoard(data.boardName, data.user)

                val currentRules = getCurrentRules(boardName = data.boardName , user = user)

                if (currentRules[0] != 'r')
                    return false //если нету прав на чтение, то ничего не получится сделать

                return currentRules[2] == 'd' && (!isPost || currentRules[3] == 'x')

            }
            else -> false
        }
    }
}