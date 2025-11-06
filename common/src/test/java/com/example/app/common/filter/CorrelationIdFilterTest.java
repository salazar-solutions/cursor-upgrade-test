package com.example.app.common.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.slf4j.MDC;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CorrelationIdFilterTest.TestConfig.class})
class CorrelationIdFilterTest {

    @Autowired
    private CorrelationIdFilter correlationIdFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        MDC.clear();
    }

    @Test
    void testDoFilter_WithExistingCorrelationId_UsesExistingId() throws ServletException, IOException {
        // Arrange
        String existingCorrelationId = "existing-correlation-id";
        request.addHeader("X-Request-Id", existingCorrelationId);
        MockFilterChain testFilterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilter(request, response, testFilterChain);

        // Assert
        assertEquals(existingCorrelationId, response.getHeader("X-Request-Id"));
        // MDC is cleared in finally block, so it should be null after filter execution
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void testDoFilter_WithoutCorrelationId_GeneratesNewId() throws ServletException, IOException {
        // Arrange
        MockFilterChain testFilterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilter(request, response, testFilterChain);

        // Assert
        String correlationId = response.getHeader("X-Request-Id");
        assertNotNull(correlationId);
        assertFalse(correlationId.isEmpty());
        assertTrue(correlationId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        // MDC is cleared in finally block, so it should be null after filter execution
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void testDoFilter_WithEmptyCorrelationId_GeneratesNewId() throws ServletException, IOException {
        // Arrange
        request.addHeader("X-Request-Id", "");
        MockFilterChain testFilterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilter(request, response, testFilterChain);

        // Assert
        String correlationId = response.getHeader("X-Request-Id");
        assertNotNull(correlationId);
        assertFalse(correlationId.isEmpty());
        assertTrue(correlationId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void testDoFilter_MDCCleanup_RemovesAfterFilter() throws ServletException, IOException {
        // Arrange
        MockFilterChain testFilterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilter(request, response, testFilterChain);

        // Assert
        // MDC should be cleared after filter execution (in finally block)
        assertNull(MDC.get("correlationId"));
    }

    @Test
    void testDoFilter_MultipleRequests_GeneratesDifferentIds() throws ServletException, IOException {
        // Arrange
        MockFilterChain testFilterChain1 = new MockFilterChain();
        MockFilterChain testFilterChain2 = new MockFilterChain();

        // Act
        correlationIdFilter.doFilter(request, response, testFilterChain1);
        String correlationId1 = response.getHeader("X-Request-Id");

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        correlationIdFilter.doFilter(request2, response2, testFilterChain2);
        String correlationId2 = response2.getHeader("X-Request-Id");

        // Assert
        assertNotNull(correlationId1);
        assertNotNull(correlationId2);
        assertNotEquals(correlationId1, correlationId2);
    }

    @Configuration
    @Import(CorrelationIdFilter.class)
    static class TestConfig {
        // Minimal configuration - only imports the filter under test
    }
}

