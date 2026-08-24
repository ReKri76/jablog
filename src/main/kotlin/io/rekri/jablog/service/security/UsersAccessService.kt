package io.rekri.jablog.service.security

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.config.security.Roles
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityAccessService
import io.rekri.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class UsersAccessService(private val customUserDetailsService: CustomUserDetailsService) : SecurityAccessService{
    override fun canAccess(data: SecurityData): Boolean {
        return when(data){
            is SecurityData.Users ->{

                val user = customUserDetailsService.loadUserByAccountNameAndBoard(data.user, data.boardName)

                if (user.boardName != data.boardName || user.role != Roles.ROLE_ADMIN)
                    return false
                return true
            }
            else -> false
        }
    }
}