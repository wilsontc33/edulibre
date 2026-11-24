package com.edulibre.api.notificacion.listener;

import com.edulibre.api.notificacion.config.AsyncConfig;
import com.edulibre.api.notificacion.event.InscripcionResueltaEvent;
import com.edulibre.api.notificacion.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InscripcionEmailListener {

    private static final Logger log = LoggerFactory.getLogger(InscripcionEmailListener.class);

    private final EmailService emailService;

    @Async(AsyncConfig.NOTIFICACION_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void alResolverInscripcion(InscripcionResueltaEvent evento) {
        try {
            emailService.enviarResultadoInscripcion(evento);
        } catch (RuntimeException ex) {
            log.error("No se pudo enviar el correo de resultado a {}", evento.email(), ex);
        }
    }
}
