package com.example.jablog.service.security

import com.example.jablog.repository.SecurityRepository
import com.example.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class DeleterAccessService(private val securityRepository: SecurityRepository) : XAccessService(securityRepository) {

    override fun canAccess(data: SecurityData): Boolean {
        return when(data){
            is SecurityData.Deleter -> {

                data.postId?.let{
                    val postId = it.toLong()
                    if (!securityRepository.isPostInBoard(data.boardName, postId))
                        return false
                }

                val isPost = !data.postId.isNullOrBlank()

                val currentRules = getCurrentRules(boardName = data.boardName , user = data.user)

                if (currentRules[0] != 'r') return false //если нету прав на чтение, то ничего не получится сделать

                return currentRules[2] == 'd' && (!isPost || currentRules[3] == 'x')

            }
            else -> false
        }
    }
}