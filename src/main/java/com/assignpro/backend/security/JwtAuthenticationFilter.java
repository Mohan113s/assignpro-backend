package com.assignpro.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtAuthenticationFilter
 *
 * Reads the "Authorization: Bearer <token>" header from every request.
 * If a valid JWT is found, populates Spring Security's context so that
 * protected endpoints (@PreAuthorize, .authenticated()) work correctly.
 *
 * Public endpoints (no JWT needed) are excluded from JWT processing:
 * POST /api/auth/login
 * POST /api/auth/register
 * GET /api/auth/verify
 * GET /
 *
 * /api/auth/me DOES require JWT and is NOT excluded.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        // Skip JWT processing for truly public endpoints
        if (isPublicPath(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // No Bearer token — pass through (Spring Security will reject protected routes)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            String email = jwtService.extractUsername(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            logger.warn("JWT expired for request: " + path);
        } catch (JwtException e) {
            logger.warn("Invalid JWT for request: " + path + " — " + e.getMessage());
        } catch (Exception e) {
            logger.error("JWT filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Returns true for paths that do NOT require JWT authentication.
     * /api/auth/me is intentionally NOT in this list — it requires a valid JWT.
     */
    private boolean isPublicPath(String path, String method) {
        // CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method))
            return true;

        return path.equals("/")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/auth/verify")
                || path.equals("/api/auth/test")
                || path.equals("/api/auth/forgot-password")
                || path.equals("/api/auth/reset-password")
                || path.equals("/error")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }
}