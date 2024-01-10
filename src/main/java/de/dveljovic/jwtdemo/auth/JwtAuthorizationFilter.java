package de.dveljovic.jwtdemo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtAuthorizationFilter(final JwtUtil jwtUtil, final ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final Optional<String> maybeAccessToken = jwtUtil.resolveToken(request);
            if(maybeAccessToken.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            final Claims claims = jwtUtil.resolveClaims(request);

            if(claims != null && jwtUtil.validateClaims(claims)) {
                final String email = claims.getSubject();
                final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, "", new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        } catch (Exception e) {
            final Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Authentication error");
            errorDetails.put("details", e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            objectMapper.writeValue(response.getWriter(), errorDetails);
        }

        filterChain.doFilter(request, response);
    }
}
