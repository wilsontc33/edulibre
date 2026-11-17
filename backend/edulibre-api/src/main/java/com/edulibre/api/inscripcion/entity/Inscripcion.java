package com.edulibre.api.inscripcion.entity;

import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.usuario.entity.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inscripcion", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "curso_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInscripcion estado = EstadoInscripcion.PENDIENTE;

    @Column(nullable = false, length = 1000)
    private String motivacion;

    @Column(name = "nivel_previo", nullable = false, length = 20)
    private String nivelPrevio;

    @Column(name = "horas_semana", nullable = false)
    private int horasSemana;

    @Column(name = "comentario_admin", length = 500)
    private String comentarioAdmin;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "resuelto_en")
    private LocalDateTime resueltoEn;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoInscripcion.PENDIENTE;
        }
    }
}
