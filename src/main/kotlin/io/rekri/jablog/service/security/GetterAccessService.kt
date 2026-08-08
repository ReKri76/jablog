package io.rekri.jablog.service.security

import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class GetterAccessService(private val securityRepository: SecurityRepository) : XAccessService(securityRepository) {

    override fun canAccess(data: SecurityData): Boolean {
        return when(data){
            is SecurityData.Getter -> {

                data.threadId?.let{
                    val threadId = it.toLong()
                    if (!securityRepository.isThreadInBoard(data.boardName, threadId))
                        return false
                }

                val isThread = !data.threadId.isNullOrBlank()

                val currentRules = getCurrentRules(boardName = data.boardName , user = data.user)

                if (currentRules[0] != 'r')
                    return false //если нету прав на чтение, то ничего не получится сделать

                return !isThread || !anyOtherFlagsIsEmpty(currentRules)
            }

            else -> false
        }
    }
}

private fun anyOtherFlagsIsEmpty(rules: String): Boolean {
    for (i in 1..<rules.length) {
        if (rules[i] != '-') return false
    }
    return true
}