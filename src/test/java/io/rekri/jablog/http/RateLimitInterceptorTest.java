package io.rekri.jablog.http;

import io.rekri.jablog.controllers.RateLimitInterceptor;
import io.rekri.jablog.service.RateLimitService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitInterceptorTest {

    private final RateLimitInterceptor interceptor = new RateLimitInterceptor(new RateLimitService());
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    void rateLimitTest() throws Exception {

        request.setRemoteAddr("192.168.1.100");
        request.setRequestURI("/api/carma/plus/b/0");

        for (int i = 0; i < 5; i++) {
            assertTrue(interceptor.preHandle(request, response, new Object()));
            response = new MockHttpServletResponse();
        }

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(429, response.getStatus());

        assertNotNull(response.getHeader("X-Rate-Limit-Retry-After-Seconds"));
        assertEquals("You have exhausted your karma Request Quota", response.getErrorMessage());
    }
}