package com.crossfit.pieds_croises.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String jwt = extractJwtFromCookie(request);

    if (org.springframework.util.StringUtils.hasText(jwt)) {
      String username = jwtService.extractClaims(jwt).getSubject();

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.validateJwtToken(jwt)) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private String extractJwtFromCookie(HttpServletRequest request) {
    return request.getCookies() != null ? Arrays.stream(request.getCookies())
        .filter(cookie -> "jwt".equals(cookie.getName()))
        .map(cookie -> cookie.getValue())
        .findFirst()
        .orElse(null)
        : null;
  }
}
