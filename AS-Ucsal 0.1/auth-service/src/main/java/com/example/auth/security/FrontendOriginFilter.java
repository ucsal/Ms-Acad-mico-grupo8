package com.example.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FrontendOriginFilter extends OncePerRequestFilter {

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console")
                || path.startsWith("/error")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        boolean originValida = origin != null && origin.startsWith(frontendUrl);
        boolean refererValido = referer != null && referer.startsWith(frontendUrl);

        if (!originValida && !refererValido) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Acesso permitido apenas pelo APP-FRONT configurado.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
