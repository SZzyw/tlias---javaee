package com.way_ne.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class CurrentUserUtils {
    private CurrentUserUtils() {
    }

    public static Integer getUserId(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return null;
        }
        Object value = claims.get("id");
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    public static String getUsername(HttpServletRequest request) {
        Claims claims = getClaims(request);
        return claims == null ? null : (String) claims.get("username");
    }

    public static Set<String> getPermissions(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return Collections.emptySet();
        }
        String permissionText = (String) claims.get("permissions");
        if (permissionText == null || permissionText.isBlank()) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(permissionText.split(",")));
    }

    private static Claims getClaims(HttpServletRequest request) {
        Object claims = request.getAttribute("claims");
        if (claims instanceof Claims jwtClaims) {
            return jwtClaims;
        }
        return null;
    }
}
