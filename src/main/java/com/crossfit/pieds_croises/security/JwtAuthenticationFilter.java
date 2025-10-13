package com.crossfit.pieds_croises.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Ignore swagger endpoints
    String path = request.getRequestURI();
    logger.info("Processing request for path: {}", path);

    if (isSwaggerEndpoint(path)) {
      logger.debug("Ignoring Swagger endpoint: {}", path);
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = extractJwtFromCookie(request);

    if (jwt == null) {
      logger.info("No JWT token found in cookies");
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String username = jwtService.extractClaims(jwt).getSubject();

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        boolean isValid = jwtService.validateJwtToken(jwt);
        logger.info("Is JWT valid? {}", isValid);

        if (isValid) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
          logger.info("Authentication set in SecurityContext for user: {}", username);
        }
      } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
        logger.info("User already authenticated: {}", SecurityContextHolder.getContext().getAuthentication().getName());
      }

    } catch (Exception e) {
      logger.error("Error during JWT filter processing: {}", e.getMessage(), e);
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("token".equals(cookie.getName())) {
          logger.info("Found token cookie with value length: {}", cookie.getValue().length());
          return cookie.getValue();
        }
      }
    }
    logger.info("No cookies found or no 'token' cookie present");
    return null;
  }

  private boolean isSwaggerEndpoint(String path) {
    return path.startsWith("/swagger-ui") ||
        path.startsWith("/v3/api-docs") ||
        path.startsWith("/swagger-resources") ||
        path.startsWith("/webjars") ||
        path.equals("/swagger-ui.html") ||
        path.equals("/favicon.ico");
  }
}
