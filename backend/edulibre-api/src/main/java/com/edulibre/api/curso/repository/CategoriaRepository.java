package com.edulibre.api.curso.repository;

import com.edulibre.api.curso.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    List<Categoria> findAllByOrderByNombreAsc();

    Optional<Categoria> findBySlug(String slug);
}
