package com.edulibre.api.notificacion.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableConfigurationProperties(NotificacionProperties.class)
public class AsyncConfig {

    public static final String NOTIFICACION_EXECUTOR = "notificacionTaskExecutor";

    @Bean(name = NOTIFICACION_EXECUTOR)
    public Executor notificacionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("notificacion-");
        executor.initialize();
        return executor;
    }
}
