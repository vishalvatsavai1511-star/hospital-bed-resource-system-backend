package com.hospital.config;

import com.hospital.model.Enums.Role;
import com.hospital.model.User;
import com.hospital.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/** Token check + simple role-based access rules for every /api/** call. */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final AuthService auth;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        String method = req.getMethod();
        String path = req.getRequestURI();
        if ("OPTIONS".equals(method) || path.startsWith("/api/auth/")) return true;

        String header = req.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7) : null;
        User user = auth.find(token);
        if (user == null) return deny(res, 401, "Please log in again");
        req.setAttribute("user", user);

        boolean write = !"GET".equals(method);
        Role role = user.getRole();
        boolean adminArea = path.startsWith("/api/users")
                || (write && (path.startsWith("/api/doctors") || path.startsWith("/api/staff") || path.startsWith("/api/beds")));
        if (adminArea && role != Role.ADMIN) return deny(res, 403, "Admin access required");
        if (write && path.startsWith("/api/reports") && role != Role.ADMIN && role != Role.DOCTOR)
            return deny(res, 403, "Only Admin or Doctor can generate reports");
        return true;
    }

    private boolean deny(HttpServletResponse res, int status, String msg) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write("{\"message\":\"" + msg + "\"}");
        return false;
    }
}
