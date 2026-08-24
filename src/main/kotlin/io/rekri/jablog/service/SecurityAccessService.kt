package io.rekri.jablog.service


interface SecurityAccessService{
    fun canAccess(data : SecurityData) : Boolean
}

sealed class SecurityData{
    data class Poster(val boardName : String, val user : String?, val threadId : String?) : SecurityData()
    data class Deleter(val boardName : String , val user : String?, val postId : String?) : SecurityData()
    data class Users(val boardName : String, val user : String) : SecurityData()
    data class Getter(val boardName : String, val user : String?, val threadId : String?) : SecurityData()
}