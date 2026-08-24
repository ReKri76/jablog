package io.rekri.jablog.config.security.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

@Component
@Slf4j
class ErrorsFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try{
            filterChain.doFilter(request, response)
        }catch (e: BadCredentialsException){
            createResponse(response, e.message?:"bad credentials", 401)
        }
        catch (e: RuntimeException){
            logger.error(e.message)
            createResponse(response, "internal server error", 500)
        }
    }

    private fun createResponse(response: HttpServletResponse, message : String, status : Int){
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write("""
                {
                    "status":$status,
                    "message":"$message",
                    "timestamp":"${Instant.now()}"
                }"""
            .trimIndent())
    }
}