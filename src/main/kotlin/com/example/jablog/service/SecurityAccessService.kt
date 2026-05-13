package com.example.jablog.service
import com.example.jablog.config.security.CustomUserDetails


interface SecurityAccessService{
    fun canAccess(data : SecurityData) : Boolean
}

sealed class SecurityData{
    data class Poster(val boardName : String, val user : CustomUserDetails, val threadId : String?) : SecurityData()
    data class Deleter(val boardName : String , val user : CustomUserDetails, val postId : String?) : SecurityData()
    data class Users(val boardName : String, val user : CustomUserDetails) : SecurityData()
    data class Getter(val boardName : String, val user : CustomUserDetails, val threadId : String?) : SecurityData()
}

