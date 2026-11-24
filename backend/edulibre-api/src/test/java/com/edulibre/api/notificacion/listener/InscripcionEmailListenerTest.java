package com.edulibre.api.notificacion.listener;

import com.edulibre.api.notificacion.event.InscripcionResueltaEvent;
import com.edulibre.api.notificacion.service.EmailService;
import com.edulibre.api.shared.util.EstadoInscripcion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InscripcionEmailListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InscripcionEmailListener listener;

    private final InscripcionResueltaEvent evento = new InscripcionResueltaEvent(
            "ana@edulibre.com",
            "Ana",
            "Desarrollo Web",
            EstadoInscripcion.APROBADA,
            null,
            "http://localhost:4200/mis-postulaciones");

    @Test
    void delegaElEnvioAlServicioDeEmail() {
        listener.alResolverInscripcion(evento);

        verify(emailService).enviarResultadoInscripcion(evento);
    }

    @Test
    void noPropagaFalloDelEnvio() {
        doThrow(new IllegalStateException("SMTP caído"))
                .when(emailService).enviarResultadoInscripcion(evento);

        assertDoesNotThrow(() -> listener.alResolverInscripcion(evento));
    }
}
