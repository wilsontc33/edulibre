package com.edulibre.api.curso.repository;

import com.edulibre.api.curso.entity.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeccionRepository extends JpaRepository<Leccion, String> {

    List<Leccion> findByCursoIdOrderByOrdenAsc(String cursoId);

    long countByCursoId(String cursoId);

    Optional<Leccion> findByIdAndCursoId(String id, String cursoId);
}
