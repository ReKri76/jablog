package io.rekri.jablog.service.security

import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class PosterAccessService(private val securityRepository: SecurityRepository,
    customUserDetailsService: CustomUserDetailsService) : XAccessService(securityRepository, customUserDetailsService) {

    override fun canAccess(data: SecurityData) : Boolean {
        return when(data){
            is SecurityData.Poster -> {

                data.threadId?.let{
                    val threadId = it.toLong()
                    if (!securityRepository.isThreadInBoard(data.boardName, threadId))
                        return false
                }

                val isThread = !data.threadId.isNullOrBlank()

                val user = loadUserByAccountNameAndBoard(data.boardName, data.user)

                val currentRules = getCurrentRules(boardName = data.boardName , user = user)

                if (currentRules[0] != 'r')
                    return false //если нету прав на чтение, то ничего не получится сделать

                return currentRules[1] == 'w' && (isThread || currentRules[3] == 'x')
            }

            else -> false
        }

    }
}