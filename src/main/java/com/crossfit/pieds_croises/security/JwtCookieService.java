package com.crossfit.pieds_croises.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtCookieService {

  public void addJwtCookie(HttpServletResponse response, String token) {
    ResponseCookie cookie = ResponseCookie.from("token", token)
        .httpOnly(true)
        // TODO: Basculer secure à true quand on sera en https pour la prod
        .secure(false)
        .path("/")
        .sameSite("Strict")
        .maxAge(7 * 24 * 60 * 60) // 7 days
        .build();

    response.addHeader("Set-Cookie", cookie.toString());
  }

  public void removeJwtCookie(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("token", "")
        .httpOnly(true)
        // TODO: Basculer secure à true quand on sera en https pour la prod
        .secure(false)
        .sameSite("Strict")
        .path("/")
        .maxAge(0)
        .build();

    response.addHeader("Set-Cookie", cookie.toString());
  }
}
