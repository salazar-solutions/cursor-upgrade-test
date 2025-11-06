package com.example.app.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Global exception handler that centralizes exception handling across all REST controllers.
 * 
 * <p>This handler provides consistent error responses for various exception types:
 * <ul>
 *   <li>Entity not found errors (404)</li>
 *   <li>Business logic violations (422)</li>
 *   <li>Validation errors (400)</li>
 *   <li>Data integrity violations (400)</li>
 *   <li>Unexpected errors (500)</li>
 * </ul>
 * 
 * <p>All error responses follow the {@link ApiError} format, including HTTP status code,
 * error type, message, request path, and timestamp.
 * 
 * <p><b>Example usage:</b>
 * <pre>{@code
 * // Throwing an exception in a controller will automatically be handled:
 * throw new EntityNotFoundException("User not found with id: " + userId);
 * // Results in: 404 Not Found response with ApiError body
 * }</pre>
 * 
 * @author Generated
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link EntityNotFoundException} when a requested entity is not found.
     * 
     * @param ex the exception containing the error message
     * @param request the web request that triggered the exception
     * @return ResponseEntity with HTTP 404 status and ApiError body
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        logger.warn("Entity not found: {}", ex.getMessage());
        ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link BusinessException} for business logic violations.
     * 
     * <p>This includes exceptions like insufficient stock, invalid state transitions,
     * or other domain-specific rule violations. Note that {@code InsufficientStockException}
     * extends {@code BusinessException} and is handled here.
     * 
     * @param ex the business exception containing the violation message
     * @param request the web request that triggered the exception
     * @return ResponseEntity with HTTP 422 (Unprocessable Entity) status and ApiError body
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, WebRequest request) {
        logger.warn("Business exception: {}", ex.getMessage());
        ApiError error = new ApiError(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Business Error",
            ex.getMessage(),
            getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} for request body validation failures.
     * 
     * <p>This occurs when {@code @Valid} annotated request bodies fail Bean Validation
     * constraints. All field errors are collected and joined into a single message.
     * 
     * @param ex the validation exception containing field errors
     * @param request the web request that triggered the exception
     * @return ResponseEntity with HTTP 400 (Bad Request) status and ApiError body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
        MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation exception: {}", ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            message,
            getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ConstraintViolationException} for path/query parameter validation failures.
     * 
     * <p>This occurs when method parameters annotated with validation constraints
     * (e.g., {@code @NotNull}, {@code @Min}) fail validation.
     * 
     * @param ex the constraint violation exception
     * @param request the web request that triggered the exception
     * @return ResponseEntity with HTTP 400 (Bad Request) status and ApiError body
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
        ConstraintViolationException ex, WebRequest request) {
        logger.warn("Constraint violation: {}", ex.getMessage());
        String message = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            message,
            getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link DataIntegrityViolationException} for database constraint violations.
     * 
     * <p>Common scenarios include unique constraint violations (duplicate keys),
     * foreign key violations, or other database-level integrity constraints.
     * 
     * @param ex the data integrity violation exception
     * @param request the web request that triggered the exception
     * @return ResponseEntity with HTTP 400 (Bad Request) status and ApiError body
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
        DataIntegrityViolationException ex, WebRequest request) {
        logger.warn("Data integrity violation: {}", ex.getMessage());
        String message = "Duplicate entry violates unique constraint";
        if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
            message = "A record with this value already exists";
        }
        
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Data Integrity Error",
            message,
            getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all unhandled exceptions as a fallback.
     * 
     * <p>This is a catch-all handler for any exception not explicitly handled above.
     * The actual exception message is not exposed to clients for security reasons;
     * a generic error message is returned instead.
     * 
     * @param ex the unhandled exception
     * @param request the web request that triggered the exception
     * @return ResponseEntity with HTTP 500 (Internal Server Error) status and ApiError body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error", ex);
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            getPath(request)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extracts the request URI path from the web request.
     * 
     * @param request the web request
     * @return the request URI path, or empty string if not available
     */
    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "";
    }
}

