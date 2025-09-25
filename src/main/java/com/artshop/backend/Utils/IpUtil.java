package com.artshop.backend.Utils;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtil {
    private IpUtil() {}
    public static String clientIp(HttpServletRequest req) {
        String h = req.getHeader("X-Forwarded-For");
        if (h != null && !h.isBlank()) return h.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}