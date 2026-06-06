package com.way_ne.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestGuardInterceptor implements HandlerInterceptor {
    private static final long WRITE_INTERVAL_MS = 800;
    private static final long LOGIN_WINDOW_MS = 60_000;
    private static final int LOGIN_MAX_ATTEMPTS = 10;

    private final Map<String, Long> latestWriteRequest = new ConcurrentHashMap<>();
    private final Map<String, ArrayDeque<Long>> loginAttempts = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        long now = Instant.now().toEpochMilli();

        if ("/login".equals(uri) && "POST".equalsIgnoreCase(method)) {
            if (!allowLogin(request.getRemoteAddr(), now)) {
                writeJson(response, 429, "{\"code\":0,\"msg\":\"登录过于频繁，请稍后再试\",\"data\":null}");
                return false;
            }
        }

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String actor = request.getHeader("token");
            if (actor == null || actor.isBlank()) {
                actor = request.getRemoteAddr();
            }
            String key = actor + ":" + method + ":" + uri;
            Long previous = latestWriteRequest.put(key, now);
            if (previous != null && now - previous < WRITE_INTERVAL_MS) {
                writeJson(response, 429, "{\"code\":0,\"msg\":\"请求过于频繁，请勿重复提交\",\"data\":null}");
                return false;
            }
            cleanupOldWrites(now);
        }

        return true;
    }

    private boolean allowLogin(String ip, long now) {
        ArrayDeque<Long> attempts = loginAttempts.computeIfAbsent(ip == null ? "unknown" : ip, k -> new ArrayDeque<>());
        while (!attempts.isEmpty() && now - attempts.peekFirst() > LOGIN_WINDOW_MS) {
            attempts.pollFirst();
        }
        if (attempts.size() >= LOGIN_MAX_ATTEMPTS) {
            return false;
        }
        attempts.addLast(now);
        return true;
    }

    private void cleanupOldWrites(long now) {
        Iterator<Map.Entry<String, Long>> iterator = latestWriteRequest.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (now - entry.getValue() > WRITE_INTERVAL_MS * 3) {
                iterator.remove();
            }
        }
    }

    private void writeJson(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(body);
    }
}
