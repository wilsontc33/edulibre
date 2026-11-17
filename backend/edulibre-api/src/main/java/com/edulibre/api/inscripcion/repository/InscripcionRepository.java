package com.edulibre.api.inscripcion.repository;

import com.edulibre.api.inscripcion.entity.Inscripcion;
import com.edulibre.api.shared.util.EstadoInscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, String> {

    @EntityGraph(attributePaths = "curso")
    List<Inscripcion> findByUsuarioIdOrderByCreadoEnDesc(String usuarioId);

    @EntityGraph(attributePaths = {"usuario", "curso"})
    List<Inscripcion> findByEstadoOrderByCreadoEnAsc(EstadoInscripcion estado);

    boolean existsByUsuarioIdAndCursoId(String usuarioId, String cursoId);

    Optional<Inscripcion> findByUsuarioIdAndCursoId(String usuarioId, String cursoId);

    boolean existsByUsuarioIdAndCursoIdAndEstado(String usuarioId, String cursoId, EstadoInscripcion estado);
}
