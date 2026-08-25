package io.rekri.jablog.unit

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.entity.Board
import io.rekri.jablog.entity.Users
import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.repository.UserDetailsRepository
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.DeleterAccessService
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
    private val mockCustomUserDetailsService = mockk<CustomUserDetailsService>()

    private val deleterAccessService = spyk(DeleterAccessService(securityRepository, mockCustomUserDetailsService))

    private val customUserDetailsService = CustomUserDetailsService(mockk<UserDetailsRepository>())

    companion object {
        private const val DEFAULT_BOARD_NAME = "default_board_name"
        private const val DEFAULT_POST_ID = "0"
        private const val DEFAULT_USER = "default_nickname"
    }

    @Test
    fun `canAccess happy path`() {
        val mockUser = createUserDetails(null)
        every {
            mockCustomUserDetailsService
                .loadUserByAccountNameAndBoard(DEFAULT_USER, DEFAULT_BOARD_NAME)
        } returns mockUser

        val deleterData = SecurityData.Deleter(
            boardName = DEFAULT_BOARD_NAME,
            user = DEFAULT_USER,
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
            user = DEFAULT_USER
        )

        val result = deleterAccessService.canAccess(otherData)

        assertFalse(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when post is not in board`() {
        val data = SecurityData.Deleter(
            boardName = DEFAULT_BOARD_NAME,
            user = null,
            postId = DEFAULT_POST_ID,
        )
        every {
            securityRepository.isPostInBoard(DEFAULT_BOARD_NAME, DEFAULT_POST_ID.toLong())
        } returns false

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
        every {
            mockCustomUserDetailsService
                .loadUserByAccountNameAndBoard(DEFAULT_USER, DEFAULT_BOARD_NAME)
        } returns mockUser

        val data = SecurityData.Deleter(DEFAULT_BOARD_NAME, DEFAULT_USER, DEFAULT_POST_ID)

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
        every {
            mockCustomUserDetailsService
                .loadUserByAccountNameAndBoard(DEFAULT_USER, DEFAULT_BOARD_NAME)
        } returns mockUser

        val data = SecurityData.Deleter(DEFAULT_BOARD_NAME, DEFAULT_USER, null)

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
            nickname = DEFAULT_USER
            password = "default_password"
            board = mockBoard
        }
        return customUserDetailsService.build(mockUsers)
    }
}