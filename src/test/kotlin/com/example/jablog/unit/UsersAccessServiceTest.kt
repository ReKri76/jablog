package com.example.jablog.unit

import com.example.jablog.config.security.CustomUserDetails
import com.example.jablog.config.security.Roles
import com.example.jablog.entity.Board
import com.example.jablog.entity.Users
import com.example.jablog.repository.UserDetailsRepository
import com.example.jablog.service.CustomUserDetailsService
import com.example.jablog.service.SecurityData
import com.example.jablog.service.security.UsersAccessService
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UsersAccessServiceTest {

    private val usersAccessService = spyk(UsersAccessService())

    private val customUserDetailsService = CustomUserDetailsService(mockk<UserDetailsRepository>())

    companion object {
        private const val DEFAULT_BOARD_NAME = "default_board_name"
    }

    @Test
    fun `canAccess happy path`() {
        val mockUser = createUserDetails().apply {
            role = Roles.ROLE_ADMIN
        }

        val usersData = SecurityData.Users(
            boardName = DEFAULT_BOARD_NAME,
            user = mockUser
        )

        val result = usersAccessService.canAccess(usersData)

        assertTrue(result)
    }

    @Test
    fun `canAccess should return false when data is not Users`() {
        val otherData = SecurityData.Poster(
            boardName = DEFAULT_BOARD_NAME,
            user = customUserDetailsService.createDefault(),
            threadId = null
        )

        val result = usersAccessService.canAccess(otherData)

        assertFalse(result)
    }

    @Test
    fun `canAccess should return false when boardName does not match`() {
        val mockUser = createUserDetails().apply {
            role = Roles.ROLE_ADMIN
        }
        val data = SecurityData.Users(
            boardName = "different_board_name",
            user = mockUser
        )

        val result = usersAccessService.canAccess(data)

        assertFalse(result)
    }

    @Test
    fun `canAccess should return false when role is not admin`() {
        val mockUser = createUserDetails().apply {
            role = Roles.ROLE_ANON
        }
        val data = SecurityData.Users(
            boardName = DEFAULT_BOARD_NAME,
            user = mockUser
        )

        val result = usersAccessService.canAccess(data)

        assertFalse(result)
    }

    private fun createUserDetails(): CustomUserDetails {
        val mockBoard = Board().apply {
            name = DEFAULT_BOARD_NAME
            rules = "rwdxrwdxrwdx"
        }
        val mockUsers = Users().apply {
            isRole = false
            nickname = "default_nickname"
            password = "default_password"
            board = mockBoard
        }
        return customUserDetailsService.build(mockUsers)
    }
}