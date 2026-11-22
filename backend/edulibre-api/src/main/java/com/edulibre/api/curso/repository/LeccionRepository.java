package com.edulibre.api.curso.repository;

import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.shared.projection.CursoConteo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeccionRepository extends JpaRepository<Leccion, String> {

    @Override
    @EntityGraph(attributePaths = "curso")
    Optional<Leccion> findById(String id);

    @Query("select coalesce(max(l.orden), 0) from Leccion l where l.curso.id = :cursoId")
    int maxOrdenByCursoId(@Param("cursoId") String cursoId);

    @Query("""
            select l.curso.id as cursoId, count(l) as total
            from Leccion l
            group by l.curso.id
            """)
    List<CursoConteo> countByCursoGrouped();

    boolean existsByCursoIdAndOrden(String cursoId, int orden);

    boolean existsByCursoIdAndOrdenAndIdNot(String cursoId, int orden, String id);

    List<Leccion> findByCursoIdOrderByOrdenAsc(String cursoId);

    long countByCursoId(String cursoId);

    Optional<Leccion> findByIdAndCursoId(String id, String cursoId);
}
