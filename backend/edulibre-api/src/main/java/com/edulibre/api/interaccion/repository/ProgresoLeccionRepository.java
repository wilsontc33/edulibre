package com.edulibre.api.interaccion.repository;

import com.edulibre.api.interaccion.entity.ProgresoLeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProgresoLeccionRepository extends JpaRepository<ProgresoLeccion, String> {

    boolean existsByUsuarioIdAndLeccionId(String usuarioId, String leccionId);

    @Query("""
            select p.leccion.id
            from ProgresoLeccion p
            where p.usuario.id = :usuarioId
              and p.leccion.curso.id = :cursoId
            """)
    List<String> findLeccionIdsByUsuarioIdAndCursoId(@Param("usuarioId") String usuarioId,
                                                     @Param("cursoId") String cursoId);
}
