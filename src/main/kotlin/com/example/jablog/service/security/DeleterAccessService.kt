package com.example.jablog.service.security

import com.example.jablog.entity.Roles
import com.example.jablog.repository.SecurityRepository
import com.example.jablog.service.PosterService
import com.example.jablog.service.SecurityAccessService
import com.example.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class DeleterAccessService(private val securityRepository: SecurityRepository)
    : SecurityAccessService{
    override fun canAccess(data: SecurityData): Boolean {
        return when(data){
            is SecurityData.Deleter -> {

                data.postId?.let{
                    val postId = it.toLong()
                    if (!securityRepository.isPostInBoard(data.boardName, postId))
                        return false
                }

                val isPost = !data.postId.isNullOrBlank()

                if (data.user.boardName.equals("ANON")) {
                    data.user.boardName = data.boardName
                    data.user.boardRules = securityRepository.getRulesByBoardName(data.boardName)
                }

                if (data.boardName != data.user.boardName)
                    data.user.role = Roles.ROLE_ANON.name

                val shift = PosterService.SIZE_OF_GROUP * when (data.user.role) {
                    Roles.ROLE_ADMIN.name -> 0
                    Roles.ROLE_GROUP.name -> 1
                    else -> 2
                }

                val currentRules = data.user.boardRules.substring(shift, shift + PosterService.SIZE_OF_GROUP)

                if (currentRules[0] != 'r') return false

                return currentRules[2] == 'd' && (!isPost || currentRules[3] == 'x')
            }
            else -> false
        }
    }
}
