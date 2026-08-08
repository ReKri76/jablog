package io.rekri.jablog.service.security

import io.rekri.jablog.config.security.Roles
import io.rekri.jablog.service.SecurityAccessService
import io.rekri.jablog.service.SecurityData
import org.springframework.stereotype.Service

@Service
class UsersAccessService : SecurityAccessService{
    override fun canAccess(data: SecurityData): Boolean {
        return when(data){
            is SecurityData.Users ->{
                if (data.user.boardName != data.boardName || data.user.role != Roles.ROLE_ADMIN)
                    return false
                return true
            }
            else -> false
        }
    }
}