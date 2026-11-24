package com.edulibre.api.notificacion.service;

import com.edulibre.api.notificacion.config.NotificacionProperties;
import com.edulibre.api.notificacion.event.InscripcionResueltaEvent;
import com.edulibre.api.shared.util.EstadoInscripcion;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private static final String PLANTILLA_APROBADA = "mail/resultado-aprobada";
    private static final String PLANTILLA_RECHAZADA = "mail/resultado-rechazada";

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final NotificacionProperties properties;

    @Override
    public void enviarResultadoInscripcion(InscripcionResueltaEvent evento) {
        boolean aprobada = evento.estado() == EstadoInscripcion.APROBADA;
        String html = renderizar(evento);

        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(properties.getFrom(), properties.getFromName());
            helper.setTo(evento.email());
            helper.setSubject(aprobada
                    ? "Tu postulación fue aprobada - " + evento.cursoTitulo()
                    : "Resultado de tu postulación - " + evento.cursoTitulo());
            helper.setText(html, true);
            javaMailSender.send(mensaje);
        } catch (MessagingException | MailException | UnsupportedEncodingException ex) {
            throw new IllegalStateException(
                    "No se pudo enviar el correo de resultado a " + evento.email(), ex);
        }
    }

    String renderizar(InscripcionResueltaEvent evento) {
        boolean aprobada = evento.estado() == EstadoInscripcion.APROBADA;

        Context context = new Context(Locale.forLanguageTag("es"));
        context.setVariable("nombre", evento.nombrePostulante());
        context.setVariable("curso", evento.cursoTitulo());
        context.setVariable("comentario", evento.comentarioAdmin());
        context.setVariable("enlace", evento.enlace());

        return templateEngine.process(aprobada ? PLANTILLA_APROBADA : PLANTILLA_RECHAZADA, context);
    }
}
