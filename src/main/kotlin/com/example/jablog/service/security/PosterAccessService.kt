package com.example.jablog.service.security

import com.example.jablog.repository.SecurityRepository
import com.example.jablog.service.PosterService
import com.example.jablog.service.SecurityAccessService
import com.example.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class PosterAccessService(private val securityRepository: SecurityRepository)
    : SecurityAccessService{

    override fun canAccess(data: SecurityData) : Boolean {
        return when(data){
            is SecurityData.Poster -> {

                data.threadId?.let{
                    val threadId = it.toLong()
                    if (!securityRepository.isThreadInBoard(data.boardName, threadId))
                        return false
                }

                val isThread = !data.threadId.isNullOrBlank()

                if (data.user.boardName.equals("ANON")) {
                    data.user.boardName = data.boardName
                    data.user.boardRules = securityRepository.getRulesByBoardName(data.boardName)
                }

                if (data.boardName != data.user.boardName)
                    data.user.role = "ROLE_ANON"

                val shift = PosterService.SIZE_OF_GROUP * when (data.user.role) {
                    "ROLE_ADMIN" -> 0
                    "ROLE_GROUP" -> 1
                    else -> 2
                }

                val currentRules = data.user.boardRules.substring(shift, shift + PosterService.SIZE_OF_GROUP)

                if (currentRules[0] != 'r') return false

                return currentRules[1] == 'w' && (isThread || currentRules[3] == 'x')
            }

            else -> false
        }

    }
}