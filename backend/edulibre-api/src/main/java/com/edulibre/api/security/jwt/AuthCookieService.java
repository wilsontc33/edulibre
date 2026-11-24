package com.edulibre.api.security.jwt;

import com.edulibre.api.security.config.AuthCookieProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthCookieService {

    private final AuthCookieProperties properties;
    private final JwtService jwtService;

    public ResponseCookie crearCookie(String token) {
        return baseCookie(token)
                .maxAge(Duration.ofMillis(jwtService.getJwtExpiration()))
                .build();
    }

    public ResponseCookie limpiarCookie() {
        return baseCookie("")
                .maxAge(Duration.ZERO)
                .build();
    }

    public Optional<String> extraerToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> properties.getName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(properties.getName(), value)
                .httpOnly(true)
                .secure(properties.isSecure())
                .sameSite(properties.getSameSite())
                .path(properties.getPath());
    }
}
