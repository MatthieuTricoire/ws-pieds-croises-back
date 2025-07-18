package com.crossfit.pieds_croises.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtCookieService {

  public void addJwtCookie(HttpServletResponse response, String token) {
    String cookieValue = "token=" + token +
        "; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=" + (7 * 24 * 60 * 60);

    response.addHeader("Set-Cookie", cookieValue);
  }

  public void removeJwtCookie(HttpServletResponse response) {
    String cookieValue = "token=; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=0";
    response.addHeader("Set-Cookie", cookieValue);
  }
}
