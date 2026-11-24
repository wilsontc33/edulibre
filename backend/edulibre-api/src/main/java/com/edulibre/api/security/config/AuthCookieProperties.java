package com.edulibre.api.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth.cookie")
public class AuthCookieProperties {

    private String name = "edulibre_token";
    private boolean secure = false;
    private String sameSite = "Lax";
    private String path = "/";
}
