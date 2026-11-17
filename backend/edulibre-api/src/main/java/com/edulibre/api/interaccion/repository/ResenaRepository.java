package com.edulibre.api.interaccion.repository;

import com.edulibre.api.interaccion.entity.Resena;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, String> {

    @EntityGraph(attributePaths = "usuario")
    List<Resena> findByCursoIdOrderByCreadoEnDesc(String cursoId);

    boolean existsByUsuarioIdAndCursoId(String usuarioId, String cursoId);
}
