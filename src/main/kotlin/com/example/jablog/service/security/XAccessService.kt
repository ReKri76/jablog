package com.example.jablog.service.security

import com.example.jablog.config.security.CustomUserDetails
import com.example.jablog.config.security.Roles
import com.example.jablog.repository.SecurityRepository
import com.example.jablog.service.PosterService
import com.example.jablog.service.SecurityAccessService

abstract class XAccessService(private val securityRepository: SecurityRepository) : SecurityAccessService{

    protected fun getCurrentRules(boardName : String, user : CustomUserDetails)
    : String{

        //мутации объекта для гарантирования консистентности данных в случае какого-либо сбоя

        if (user.boardName.equals("ANON")) {
            user.boardName = boardName
            user.boardRules = securityRepository.getRulesByBoardName(boardName)
        }

        if (boardName != user.boardName)
            user.role = Roles.ROLE_ANON

        /**
         * см [com.example.jablog.entity.Board]
         * */
        val shift = PosterService.SIZE_OF_GROUP * user.role.ordinal

        val currentRules = user.boardRules.substring(shift, shift + PosterService.SIZE_OF_GROUP)

        return currentRules
    }
}