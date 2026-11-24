package com.edulibre.api.notificacion.service;

import com.edulibre.api.notificacion.event.InscripcionResueltaEvent;

public interface EmailService {

    void enviarResultadoInscripcion(InscripcionResueltaEvent evento);
}
