package io.rekri.jablog.service.security

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.config.security.Roles
import io.rekri.jablog.entity.Board
import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityAccessService

abstract class XAccessService(private val securityRepository: SecurityRepository,
    private val customUserDetailsService: CustomUserDetailsService) : SecurityAccessService{

    protected fun getCurrentRules(boardName : String, user : CustomUserDetails) : String{

        //мутации объекта для гарантирования консистентности данных в случае какого-либо сбоя

        if (user.boardName.equals("ANON")) {
            user.boardName = boardName
            user.boardRules = securityRepository.getRulesByBoardName(boardName)
        }

        if (boardName != user.boardName)
            user.role = Roles.ROLE_ANON

        /**
         * см [io.rekri.jablog.entity.Board]
         * */
        val shift = Board.SIZE_OF_GROUP * user.role.ordinal

        val currentRules = user.boardRules.substring(shift, shift + Board.SIZE_OF_GROUP)

        return currentRules
    }

    protected fun loadUserByAccountNameAndBoard(boardName : String, user : String?) : CustomUserDetails {
        return if (user != null)
            customUserDetailsService.loadUserByAccountNameAndBoard(user, boardName)
        else
            customUserDetailsService.createDefault()
    }
}