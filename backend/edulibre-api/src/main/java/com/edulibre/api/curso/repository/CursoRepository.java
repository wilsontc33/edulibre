package com.edulibre.api.curso.repository;

import com.edulibre.api.curso.entity.Curso;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, String> {

    @EntityGraph(attributePaths = "categoria")
    List<Curso> findByPublicadoTrueOrderByCreadoEnDesc();

    @EntityGraph(attributePaths = "categoria")
    List<Curso> findByPublicadoTrueAndCategoriaSlugOrderByCreadoEnDesc(String slug);

    @EntityGraph(attributePaths = "categoria")
    Optional<Curso> findByIdAndPublicadoTrue(String id);
}
