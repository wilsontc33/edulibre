-- 1. TABLA USUARIO
CREATE TABLE usuario (
    id CHAR(36) PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL, -- ADMIN, ESTUDIANTE
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

-- 2. TABLA CATEGORIA
CREATE TABLE categoria (
    id CHAR(36) PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    slug VARCHAR(80) NOT NULL UNIQUE
);

-- 3. TABLA CURSO
CREATE TABLE curso (
    id CHAR(36) PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    categoria_id CHAR(36) NOT NULL,
    instructor VARCHAR(120) NOT NULL,
    imagen_url VARCHAR(500),
    publicado BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_curso_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

-- 4. TABLA LECCION
CREATE TABLE leccion (
    id CHAR(36) PRIMARY KEY,
    curso_id CHAR(36) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    orden INT NOT NULL,
    youtube_id VARCHAR(20) NOT NULL,
    duracion_min INT,
    CONSTRAINT fk_leccion_curso FOREIGN KEY (curso_id) REFERENCES curso(id) ON DELETE CASCADE,
    CONSTRAINT uq_curso_orden UNIQUE (curso_id, orden)
);

-- 5. TABLA INSCRIPCION
CREATE TABLE inscripcion (
    id CHAR(36) PRIMARY KEY,
    usuario_id CHAR(36) NOT NULL,
    curso_id CHAR(36) NOT NULL,
    estado VARCHAR(20) NOT NULL, -- PENDIENTE, APROBADA, RECHAZADA
    motivacion VARCHAR(1000) NOT NULL,
    nivel_previo VARCHAR(20) NOT NULL,
    horas_semana INT NOT NULL,
    comentario_admin VARCHAR(500),
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    resuelto_en DATETIME(6),
    CONSTRAINT fk_inscripcion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_inscripcion_curso FOREIGN KEY (curso_id) REFERENCES curso(id),
    CONSTRAINT uq_usuario_curso UNIQUE (usuario_id, curso_id)
);

-- 6. TABLA RESENA
CREATE TABLE resena (
    id CHAR(36) PRIMARY KEY,
    curso_id CHAR(36) NOT NULL,
    usuario_id CHAR(36) NOT NULL,
    calificacion INT NOT NULL,
    comentario VARCHAR(500) NOT NULL,
    creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_resena_curso FOREIGN KEY (curso_id) REFERENCES curso(id),
    CONSTRAINT fk_resena_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- 7. TABLA PROGRESO_LECCION
CREATE TABLE progreso_leccion (
    id CHAR(36) PRIMARY KEY,
    usuario_id CHAR(36) NOT NULL,
    leccion_id CHAR(36) NOT NULL,
    completado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_progreso_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_progreso_leccion FOREIGN KEY (leccion_id) REFERENCES leccion(id) ON DELETE CASCADE,
    CONSTRAINT uq_usuario_leccion UNIQUE (usuario_id, leccion_id)
);