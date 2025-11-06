package com.example.app.common.filter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that manages correlation IDs for request tracing.
 * 
 * <p>This filter extracts or generates a correlation ID for each HTTP request and:
 * <ul>
 *   <li>Adds it to the response header as "X-Request-Id"</li>
 *   <li>Stores it in SLF4J's MDC (Mapped Diagnostic Context) for logging</li>
 * </ul>
 * 
 * <p>If the request already contains an "X-Request-Id" header, that value is used.
 * Otherwise, a new UUID is generated. The correlation ID is available in all log
 * statements during request processing via MDC.
 * 
 * <p><b>Usage in logging:</b>
 * <pre>{@code
 * // In logback.xml or log4j2.xml, include %X{correlationId} in pattern
 * // Example: %d{yyyy-MM-dd HH:mm:ss} [%X{correlationId}] %-5level %logger{36} - %msg%n
 * }</pre>
 * 
 * <p><b>Thread Safety:</b> MDC is thread-local, so each request thread has its own
 * correlation ID. The filter cleans up MDC after request processing completes.
 * 
 * @author Generated
 * @since 1.0.0
 */
@Component
@Order(1)
public class CorrelationIdFilter implements Filter {
    private static final String CORRELATION_ID_HEADER = "X-Request-Id";
    private static final String MDC_CORRELATION_ID = "correlationId";

    /**
     * Initializes the filter. No action required.
     * 
     * @param filterConfig filter configuration (unused)
     * @throws ServletException never thrown
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    /**
     * Processes the request to extract or generate a correlation ID.
     * 
     * <p>Extracts "X-Request-Id" from request header, or generates a new UUID if absent.
     * Sets the correlation ID in MDC and response header, then continues the filter chain.
     * MDC is cleaned up after request processing.
     * 
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain to continue processing
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_CORRELATION_ID, correlationId);
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    /**
     * Destroys the filter. No cleanup required.
     */
    @Override
    public void destroy() {
        // No cleanup needed
    }
}

