package com.edulibre.api.notificacion.service;

import com.edulibre.api.notificacion.config.NotificacionProperties;
import com.edulibre.api.notificacion.event.InscripcionResueltaEvent;
import com.edulibre.api.shared.util.EstadoInscripcion;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmtpEmailServiceTest {

    private static final String ENLACE = "http://localhost:4200/mis-postulaciones";

    private JavaMailSender javaMailSender;
    private SmtpEmailService smtpEmailService;

    @BeforeEach
    void setUp() {
        javaMailSender = mock(JavaMailSender.class);

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode("HTML");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        NotificacionProperties properties = new NotificacionProperties();
        properties.setFrom("no-reply@edulibre.test");
        properties.setFromName("EduLibre Test");
        properties.setFrontendUrl("http://localhost:4200");

        smtpEmailService = new SmtpEmailService(javaMailSender, templateEngine, properties);
    }

    @Test
    void renderizaPlantillaAprobadaConDatosDelEvento() {
        String html = smtpEmailService.renderizar(evento(EstadoInscripcion.APROBADA, null));

        assertTrue(html.contains("Ana"));
        assertTrue(html.contains("Desarrollo Web"));
        assertTrue(html.contains("APROBADA"));
        assertTrue(html.contains(ENLACE));
        assertFalse(html.contains("Comentario del administrador"));
    }

    @Test
    void renderizaPlantillaRechazadaConComentario() {
        String html = smtpEmailService.renderizar(
                evento(EstadoInscripcion.RECHAZADA, "No cumple los requisitos"));

        assertTrue(html.contains("RECHAZADA"));
        assertTrue(html.contains("Comentario del administrador"));
        assertTrue(html.contains("No cumple los requisitos"));
        assertFalse(html.contains("APROBADA"));
    }

    @Test
    void omiteBloqueDeComentarioCuandoEstaVacio() {
        String html = smtpEmailService.renderizar(evento(EstadoInscripcion.RECHAZADA, "  "));

        assertTrue(html.contains("RECHAZADA"));
        assertFalse(html.contains("Comentario del administrador"));
    }

    @Test
    void enviaCorreoConRemitenteDestinatarioYAsunto() throws Exception {
        MimeMessage mensaje = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(mensaje);

        smtpEmailService.enviarResultadoInscripcion(evento(EstadoInscripcion.APROBADA, null));

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());

        MimeMessage enviado = captor.getValue();
        assertEquals("Tu postulación fue aprobada - Desarrollo Web", enviado.getSubject());
        assertEquals("ana@edulibre.com", enviado.getAllRecipients()[0].toString());
        assertTrue(enviado.getFrom()[0].toString().contains("no-reply@edulibre.test"));

        Object contenido = enviado.getContent();
        String html = contenido instanceof MimeMultipart multipart
                ? (String) multipart.getBodyPart(0).getContent()
                : contenido.toString();
        assertTrue(html.contains("Desarrollo Web"));
    }

    private InscripcionResueltaEvent evento(EstadoInscripcion estado, String comentario) {
        return new InscripcionResueltaEvent(
                "ana@edulibre.com",
                "Ana",
                "Desarrollo Web",
                estado,
                comentario,
                ENLACE);
    }
}
