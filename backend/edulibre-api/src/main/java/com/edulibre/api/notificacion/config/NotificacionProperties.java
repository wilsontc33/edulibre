package com.edulibre.api.notificacion.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.mail")
public class NotificacionProperties {

    private String from = "no-reply@edulibre.com";
    private String fromName = "EduLibre";
    private String frontendUrl = "http://localhost:4200";
}
