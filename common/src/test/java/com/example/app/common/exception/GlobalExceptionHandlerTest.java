package com.example.app.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/test");
        webRequest = new ServletWebRequest(request);
    }

    @Test
    void testHandleEntityNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("User not found");
        ResponseEntity<ApiError> response = handler.handleEntityNotFound(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void testHandleBusinessException() {
        BusinessException ex = new BusinessException("Insufficient stock");
        ResponseEntity<ApiError> response = handler.handleBusinessException(ex, webRequest);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().getStatus());
        assertEquals("Business Error", response.getBody().getError());
        assertEquals("Insufficient stock", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");
        ResponseEntity<ApiError> response = handler.handleGenericException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
    }
}

