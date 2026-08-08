package io.rekri.jablog.unit

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.entity.Board
import io.rekri.jablog.entity.Users
import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.repository.UserDetailsRepository
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.PosterAccessService
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PosterAccessServiceTest {

    private val securityRepository = mockk<SecurityRepository>()

    private val posterAccessService = spyk(PosterAccessService(securityRepository))

    private val customUserDetailsService = CustomUserDetailsService(mockk<UserDetailsRepository>())

    companion object {
        private const val DEFAULT_BOARD_NAME = "default_board_name"
        private const val DEFAULT_THREAD_ID = "0"
    }

    @Test
    fun `canAccess happy path`() {
        val mockUser = createUserDetails(null)

        val posterData = SecurityData.Poster(
            boardName = DEFAULT_BOARD_NAME,
            user = mockUser,
            threadId = null,
        )

        val result = posterAccessService.canAccess(posterData)

        assertTrue(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when data is not Poster`() {
        val otherData = SecurityData.Users(
            boardName = DEFAULT_BOARD_NAME,
            user = customUserDetailsService.createDefault()
        )

        val result = posterAccessService.canAccess(otherData)

        assertFalse(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when thread is not in board`() {
        val data = SecurityData.Poster(
            boardName = DEFAULT_BOARD_NAME,
            user = customUserDetailsService.createDefault(),
            threadId = DEFAULT_THREAD_ID,
            )
        every { securityRepository.isThreadInBoard(DEFAULT_BOARD_NAME, DEFAULT_THREAD_ID.toLong()) } returns false

        val result = posterAccessService.canAccess(data)

        assertFalse(result)
        verify(exactly = 1) { securityRepository.isThreadInBoard(any(), any()) }
    }

    @ParameterizedTest(name = "threadId: ''$DEFAULT_THREAD_ID'', rules: ''{1}'' => expected access: {2}")
    @CsvSource(
        "-w-x,    false", // Нет прав на чтение
        "r--x,    false", // Есть чтение, нет записи
        "rw--,    true",  // Запись в тред: есть чтение и запись
        "xrwd,    false"  // Просто некорректный набор символов
    )
    fun `canAccess should validate combinations of rules by thread`(
        rules: String,
        expectedResult: Boolean
    ) {
        val mockUser = createUserDetails("rwdx"+rules+"rwdx")
        val data = SecurityData.Poster(DEFAULT_BOARD_NAME, mockUser, DEFAULT_THREAD_ID)

        every { securityRepository.isThreadInBoard(DEFAULT_BOARD_NAME, DEFAULT_THREAD_ID.toLong()) } returns true

        val result = posterAccessService.canAccess(data)

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest(name = "threadId: ''empty'', rules: ''{0}'' => expected access: {1}")
    @CsvSource(
        "rw--,    false", // Создание треда (threadId пуст): нет прав
        "rw-x,    true",  // Создание треда (threadId пуст): есть права rw и x
        "r--x,    false", // Создание треда: нет прав на запись
        "xrwd,    false"  // Просто некорректный набор символов
    )
    fun `canAccess should validate combinations of rules without thread`(
        rules: String,
        expectedResult: Boolean
    ) {
        val mockUser = createUserDetails("rwdx"+rules+"rwdx")
        val data = SecurityData.Poster(DEFAULT_BOARD_NAME, mockUser, null)


        val result = posterAccessService.canAccess(data)

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