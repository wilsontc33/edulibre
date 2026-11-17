-- Ajustes derivados del contrato con el frontend Angular.
-- No se modifica V1: los cambios van en una migración nueva.

ALTER TABLE curso
    ADD COLUMN nivel VARCHAR(20) NOT NULL DEFAULT 'Básico',
    ADD COLUMN duracion_semanas INT NULL;

ALTER TABLE resena
    ADD CONSTRAINT uq_resena_usuario_curso UNIQUE (usuario_id, curso_id);
