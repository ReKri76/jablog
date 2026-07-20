package com.example.jablog.unit

import com.example.jablog.config.security.CustomUserDetails
import com.example.jablog.entity.Board
import com.example.jablog.entity.Users
import com.example.jablog.repository.SecurityRepository
import com.example.jablog.repository.UserDetailsRepository
import com.example.jablog.service.CustomUserDetailsService
import com.example.jablog.service.SecurityData
import com.example.jablog.service.security.DeleterAccessService
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DeleterAccessServiceTest {

    private val securityRepository = mockk<SecurityRepository>()

    private val deleterAccessService = spyk(DeleterAccessService(securityRepository))

    private val customUserDetailsService = CustomUserDetailsService(mockk<UserDetailsRepository>())

    companion object {
        private const val DEFAULT_BOARD_NAME = "default_board_name"
        private const val DEFAULT_POST_ID = "0"
    }

    @Test
    fun `canAccess happy path`() {
        val mockUser = createUserDetails(null)

        val deleterData = SecurityData.Deleter(
            boardName = DEFAULT_BOARD_NAME,
            user = mockUser,
            postId = null,
        )

        val result = deleterAccessService.canAccess(deleterData)

        assertTrue(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when data is not Deleter`() {
        val otherData = SecurityData.Users(
            boardName = DEFAULT_BOARD_NAME,
            user = customUserDetailsService.createDefault()
        )

        val result = deleterAccessService.canAccess(otherData)

        assertFalse(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when post is not in board`() {
        val data = SecurityData.Deleter(
            boardName = DEFAULT_BOARD_NAME,
            user = customUserDetailsService.createDefault(),
            postId = DEFAULT_POST_ID,
        )
        every { securityRepository.isPostInBoard(DEFAULT_BOARD_NAME, DEFAULT_POST_ID.toLong()) } returns false

        val result = deleterAccessService.canAccess(data)

        assertFalse(result)
        verify(exactly = 1) { securityRepository.isPostInBoard(any(), any()) }
    }

    @ParameterizedTest(name = "postId: ''$DEFAULT_POST_ID'', rules: ''{1}'' => expected access: {2}")
    @CsvSource(
        "-w-x,    false", // Нет прав на чтение
        "r-d-,    false", // Есть чтение и удаление, нет x
        "r-dx,    true",  // Удаление поста: есть чтение, удаление и x
        "xrwd,    false"  // Просто некорректный набор символов
    )
    fun `canAccess should validate combinations of rules by post`(
        rules: String,
        expectedResult: Boolean
    ) {
        val mockUser = createUserDetails("rwdx"+rules+"rwdx")
        val data = SecurityData.Deleter(DEFAULT_BOARD_NAME, mockUser, DEFAULT_POST_ID)

        every { securityRepository.isPostInBoard(DEFAULT_BOARD_NAME, DEFAULT_POST_ID.toLong()) } returns true

        val result = deleterAccessService.canAccess(data)

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest(name = "postId: ''empty'', rules: ''{0}'' => expected access: {1}")
    @CsvSource(
        "r-d-,    true",  // Удаление без указания поста: есть чтение и d
        "r-dx,    true",  // Удаление без указания поста: есть чтение, d и x
        "--d-,    false", // Нет прав на чтение
        "r-w-,    false"  // Нет прав на удаление
    )
    fun `canAccess should validate combinations of rules without post`(
        rules: String,
        expectedResult: Boolean
    ) {
        val mockUser = createUserDetails("rwdx"+rules+"rwdx")
        val data = SecurityData.Deleter(DEFAULT_BOARD_NAME, mockUser, null)

        val result = deleterAccessService.canAccess(data)

        assertEquals(expectedResult, result)
    }

    private fun createUserDetails(rule : String?): CustomUserDetails {
        val mockBoard = Board().apply {
            name = DEFAULT_BOARD_NAME
            rules = rule?: "rwdxrwdxrwdx"
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