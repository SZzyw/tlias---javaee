package com.way_ne.security;

import com.way_ne.utils.CurrentUserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Set;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresPermission permission = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        if (permission == null) {
            permission = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
        }
        if (permission == null) {
            return true;
        }

        Set<String> permissionCodes = CurrentUserUtils.getPermissions(request);
        for (String required : permission.value()) {
            if (permissionCodes.contains(required)) {
                return true;
            }
        }

        writeJson(response, HttpServletResponse.SC_FORBIDDEN, "{\"code\":0,\"msg\":\"无权限访问\",\"data\":null}");
        return false;
    }

    private void writeJson(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(body);
    }
}
