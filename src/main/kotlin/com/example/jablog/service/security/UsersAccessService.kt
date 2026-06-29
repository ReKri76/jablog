package com.example.jablog.service.security

import com.example.jablog.config.security.Roles
import com.example.jablog.service.SecurityAccessService
import com.example.jablog.service.SecurityData
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