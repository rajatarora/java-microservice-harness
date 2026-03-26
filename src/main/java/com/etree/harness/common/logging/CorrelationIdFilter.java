package com.etree.harness.common.logging;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID = "requestId";
    public static final String TRACE_ID = "traceId";

    /**
     * Ensures each request has a request id and copies relevant trace headers
     * into the SLF4J Mapped Diagnostic Context (MDC) so they appear in logs.
     *
     * <p>This method will generate a UUID when the incoming request does not
     * contain an `X-Request-Id` header and will attempt to copy common tracing
     * headers such as `traceparent` and `X-B3-TraceId` into the MDC.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = Optional.ofNullable(request.getHeader("X-Request-Id")).orElse(UUID.randomUUID().toString());
        MDC.put(REQUEST_ID, requestId);

        // Copy common tracing headers if present
        Optional.ofNullable(request.getHeader("traceparent")).ifPresent(tp -> MDC.put(TRACE_ID, tp));
        Optional.ofNullable(request.getHeader("X-B3-TraceId")).ifPresent(t -> MDC.put(TRACE_ID, t));

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID);
            MDC.remove(TRACE_ID);
        }
    }
}
