package io.rekri.jablog.unit

import io.rekri.jablog.config.security.CustomUserDetails
import io.rekri.jablog.entity.Board
import io.rekri.jablog.entity.Users
import io.rekri.jablog.repository.SecurityRepository
import io.rekri.jablog.repository.UserDetailsRepository
import io.rekri.jablog.service.CustomUserDetailsService
import io.rekri.jablog.service.SecurityData
import io.rekri.jablog.service.security.GetterAccessService
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class GetterAccessServiceTest {

    private val securityRepository = mockk<SecurityRepository>()
    private val mockCustomUserDetailsService = mockk<CustomUserDetailsService>()

    private val getterAccessService = spyk(GetterAccessService(securityRepository, mockCustomUserDetailsService))

    private val customUserDetailsService = CustomUserDetailsService(mockk<UserDetailsRepository>())

    companion object {
        private const val DEFAULT_BOARD_NAME = "default_board_name"
        private const val DEFAULT_THREAD_ID = "0"
        private const val DEFAULT_USER = "default_nickname"
    }

    @Test
    fun `canAccess happy path`() {
        val mockUser = createUserDetails(null)
        every {
            mockCustomUserDetailsService
                .loadUserByAccountNameAndBoard(DEFAULT_USER, DEFAULT_BOARD_NAME)
        } returns mockUser

        val getterData = SecurityData.Getter(
            boardName = DEFAULT_BOARD_NAME,
            user = DEFAULT_USER,
            threadId = null,
        )

        val result = getterAccessService.canAccess(getterData)

        assertTrue(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when data is not Getter`() {
        val otherData = SecurityData.Users(
            boardName = DEFAULT_BOARD_NAME,
            user = DEFAULT_USER
        )

        val result = getterAccessService.canAccess(otherData)

        assertFalse(result)
        verify { securityRepository wasNot Called }
    }

    @Test
    fun `canAccess should return false when thread is not in board`() {
        val data = SecurityData.Getter(
            boardName = DEFAULT_BOARD_NAME,
            user = null,
            threadId = DEFAULT_THREAD_ID,
        )
        every {
            securityRepository.isThreadInBoard(DEFAULT_BOARD_NAME, DEFAULT_THREAD_ID.toLong())
        } returns false

        val result = getterAccessService.canAccess(data)

        assertFalse(result)
        verify(exactly = 1) { securityRepository.isThreadInBoard(any(), any()) }
    }

    @ParameterizedTest(name = "threadId: ''$DEFAULT_THREAD_ID'', rules: ''{1}'' => expected access: {2}")
    @CsvSource(
        "---x,    false", // Нет прав на чтение
        "r---,    false", // Есть чтение, но остальные флаги пустые
        "r-d-,    true",  // Чтение треда: есть чтение и хотя бы один другой флаг не пустой
        "rw--,    true"   // Чтение треда: есть чтение и хотя бы один другой флаг не пустой
    )
    fun `canAccess should validate combinations of rules by thread`(
        rules: String,
        expectedResult: Boolean
    ) {
        val mockUser = createUserDetails("rwdx"+rules+"rwdx")
        every {
            mockCustomUserDetailsService
                .loadUserByAccountNameAndBoard(DEFAULT_USER, DEFAULT_BOARD_NAME)
        } returns mockUser

        val data = SecurityData.Getter(DEFAULT_BOARD_NAME, DEFAULT_USER, DEFAULT_THREAD_ID)

        every {
            securityRepository.isThreadInBoard(DEFAULT_BOARD_NAME, DEFAULT_THREAD_ID.toLong())
        } returns true

        val result = getterAccessService.canAccess(data)

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest(name = "threadId: ''empty'', rules: ''{0}'' => expected access: {1}")
    @CsvSource(
        "r---,    true",  // Есть чтение, threadId пуст
        "----,    false"  // Нет прав на чтение
    )
    fun `canAccess should validate combinations of rules without thread`(
        rules: String,
        expectedResult: Boolean
    ) {
        val mockUser = createUserDetails("rwdx"+rules+"rwdx")
        every {
            mockCustomUserDetailsService
                .loadUserByAccountNameAndBoard(DEFAULT_USER, DEFAULT_BOARD_NAME)
        } returns mockUser

        val data = SecurityData.Getter(DEFAULT_BOARD_NAME, DEFAULT_USER, null)

        val result = getterAccessService.canAccess(data)

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