package com.ib.poc.whatsapp.infrastructure.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs every inbound HTTP request and its response status + duration.
 * Runs once per request before the controller layer.
 */
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String method      = request.getMethod();
        String uri         = request.getRequestURI();
        String query       = request.getQueryString();
        String contentType = request.getContentType();
        String remoteAddr  = request.getRemoteAddr();
        String userAgent   = request.getHeader("User-Agent");

        log.info("--> {} {}{} | Content-Type={} | Remote={} | User-Agent={}",
                method,
                uri,
                query != null ? "?" + query : "",
                contentType,
                remoteAddr,
                userAgent);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("<-- {} {} | Status={} | {}ms", method, uri, response.getStatus(), duration);
        }
    }
}
