package com.edulibre.api.inscripcion;

import com.edulibre.api.curso.entity.Categoria;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.inscripcion.dto.AdminInscripcionResponse;
import com.edulibre.api.inscripcion.dto.CrearInscripcionRequest;
import com.edulibre.api.inscripcion.dto.InscripcionResponse;
import com.edulibre.api.inscripcion.entity.Inscripcion;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.inscripcion.service.InscripcionService;
import com.edulibre.api.notificacion.config.NotificacionProperties;
import com.edulibre.api.notificacion.event.InscripcionResueltaEvent;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.shared.util.Rol;
import com.edulibre.api.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NotificacionProperties notificacionProperties;

    @InjectMocks
    private InscripcionService inscripcionService;

    private final Usuario usuario = Usuario.builder()
            .id("u-1")
            .nombre("Ana")
            .email("ana@edulibre.com")
            .rol(Rol.ESTUDIANTE)
            .build();

    private final Curso curso = Curso.builder()
            .id("curso-1")
            .titulo("Desarrollo Web")
            .categoria(Categoria.builder().id("cat-1").nombre("Dev").slug("dev").build())
            .instructor("Prof")
            .publicado(true)
            .build();

    @Test
    void crearInscripcionQuedaPendiente() {
        when(currentUserService.requireCurrentUser()).thenReturn(usuario);
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(inscripcionRepository.existsByUsuarioIdAndCursoId("u-1", "curso-1")).thenReturn(false);
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(invocation -> {
            Inscripcion inscripcion = invocation.getArgument(0);
            inscripcion.setId("insc-1");
            inscripcion.setCreadoEn(LocalDateTime.now());
            return inscripcion;
        });

        InscripcionResponse response = inscripcionService.crear(
                new CrearInscripcionRequest("curso-1", "Quiero aprender", "Básico", 5));

        assertEquals("insc-1", response.id());
        assertEquals("PENDIENTE", response.estado());
        assertEquals("Desarrollo Web", response.curso());
    }

    @Test
    void crearInscripcionRechazaDuplicado() {
        when(currentUserService.requireCurrentUser()).thenReturn(usuario);
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(inscripcionRepository.existsByUsuarioIdAndCursoId("u-1", "curso-1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> inscripcionService.crear(
                new CrearInscripcionRequest("curso-1", "Quiero aprender", "Básico", 5)));
    }

    @Test
    void aprobarRechazaInscripcionYaResuelta() {
        Inscripcion inscripcion = Inscripcion.builder()
                .id("insc-1")
                .usuario(usuario)
                .curso(curso)
                .estado(EstadoInscripcion.APROBADA)
                .build();
        when(inscripcionRepository.findById("insc-1")).thenReturn(Optional.of(inscripcion));

        assertThrows(ConflictException.class, () -> inscripcionService.aprobar("insc-1"));
    }

    @Test
    void rechazarGuardaComentarioYEstado() {
        Inscripcion inscripcion = Inscripcion.builder()
                .id("insc-1")
                .usuario(usuario)
                .curso(curso)
                .estado(EstadoInscripcion.PENDIENTE)
                .nivelPrevio("Básico")
                .motivacion("Quiero aprender")
                .build();
        when(inscripcionRepository.findById("insc-1")).thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificacionProperties.getFrontendUrl()).thenReturn("http://localhost:4200");

        AdminInscripcionResponse response = inscripcionService.rechazar("insc-1", "Sin cupos");

        assertEquals("RECHAZADA", response.estado());
        assertEquals("Sin cupos", response.comentarioAdmin());

        ArgumentCaptor<InscripcionResueltaEvent> captor =
                ArgumentCaptor.forClass(InscripcionResueltaEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertEquals(EstadoInscripcion.RECHAZADA, captor.getValue().estado());
        assertEquals("Sin cupos", captor.getValue().comentarioAdmin());
        assertEquals("http://localhost:4200/mis-postulaciones", captor.getValue().enlace());
    }

    @Test
    void aprobarPublicaEventoDeResultado() {
        Inscripcion inscripcion = Inscripcion.builder()
                .id("insc-1")
                .usuario(usuario)
                .curso(curso)
                .estado(EstadoInscripcion.PENDIENTE)
                .nivelPrevio("Básico")
                .motivacion("Quiero aprender")
                .build();
        when(inscripcionRepository.findById("insc-1")).thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(notificacionProperties.getFrontendUrl()).thenReturn("http://localhost:4200/");

        inscripcionService.aprobar("insc-1");

        ArgumentCaptor<InscripcionResueltaEvent> captor =
                ArgumentCaptor.forClass(InscripcionResueltaEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        InscripcionResueltaEvent evento = captor.getValue();
        assertEquals("ana@edulibre.com", evento.email());
        assertEquals("Ana", evento.nombrePostulante());
        assertEquals("Desarrollo Web", evento.cursoTitulo());
        assertEquals(EstadoInscripcion.APROBADA, evento.estado());
        assertNull(evento.comentarioAdmin());
        assertEquals("http://localhost:4200/mis-postulaciones", evento.enlace());
    }
}
