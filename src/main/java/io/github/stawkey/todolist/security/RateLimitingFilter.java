package io.github.stawkey.todolist.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitingFilter implements Filter {

    private final Map<String, AtomicInteger> requestCountsPerIpAddress = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS_PER_MINUTE = 30;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String clientIpAddress = httpServletRequest.getRemoteAddr();

        requestCountsPerIpAddress.putIfAbsent(clientIpAddress, new AtomicInteger(0));
        AtomicInteger requestCount = requestCountsPerIpAddress.get(clientIpAddress);

        int requests = requestCount.incrementAndGet();

        if (requests > MAX_REQUESTS_PER_MINUTE) {
            httpServletResponse.setStatus(429);
            httpServletResponse.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }
}
