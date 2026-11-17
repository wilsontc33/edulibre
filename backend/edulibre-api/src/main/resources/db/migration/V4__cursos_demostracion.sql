-- Datos de demostración para que el catálogo no arranque vacío.
-- Coinciden con los cursos que hoy muestra el frontend Angular.
-- Puedes eliminar este archivo si vas a cargar cursos reales.

SET @cat_desarrollo := (SELECT id FROM categoria WHERE slug = 'desarrollo-web');
SET @cat_ia := (SELECT id FROM categoria WHERE slug = 'inteligencia-artificial');
SET @cat_ux := (SELECT id FROM categoria WHERE slug = 'diseno-ux-ui');
SET @cat_datos := (SELECT id FROM categoria WHERE slug = 'ciencia-de-datos');
SET @cat_marketing := (SELECT id FROM categoria WHERE slug = 'marketing-digital');

INSERT INTO curso (id, titulo, descripcion, categoria_id, instructor, imagen_url, publicado, nivel, duracion_semanas) VALUES
    (UUID(), 'Desarrollo Web Full Stack', 'React & Node desde cero.',
     @cat_desarrollo, 'Prof. Carlos Mendoza',
     'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&q=80&w=600&h=300',
     TRUE, 'Básico', 12),
    (UUID(), 'Fundamentos de IA Generativa', 'Prompting y LLMs.',
     @cat_ia, 'Prof. Laura Ríos',
     'https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&q=80&w=600&h=300',
     TRUE, 'Intermedio', 6),
    (UUID(), 'Diseño UX/UI Profesional', 'Prototipado en Figma.',
     @cat_ux, 'Prof. Diego Torres',
     'https://images.unsplash.com/photo-1561070791-2526d30994b5?auto=format&fit=crop&q=80&w=600&h=300',
     TRUE, 'Básico', 8),
    (UUID(), 'Python para Análisis de Datos', 'Pandas y visualización.',
     @cat_datos, 'Prof. Sofía Herrera',
     'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&q=80&w=600&h=300',
     TRUE, 'Intermedio', 10),
    (UUID(), 'Arquitectura Cloud & DevOps', 'Docker y Kubernetes.',
     @cat_desarrollo, 'Prof. Andrés Vega',
     'https://images.unsplash.com/photo-1667372393119-3d4c48d07fc9?auto=format&fit=crop&q=80&w=600&h=300',
     TRUE, 'Avanzado', 8),
    (UUID(), 'Growth Marketing Digital', 'Estrategias de adquisición.',
     @cat_marketing, 'Prof. Camila Duarte',
     'https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&q=80&w=600&h=300',
     TRUE, 'Básico', 6);

SET @curso_web := (SELECT id FROM curso WHERE titulo = 'Desarrollo Web Full Stack' LIMIT 1);
SET @curso_ia := (SELECT id FROM curso WHERE titulo = 'Fundamentos de IA Generativa' LIMIT 1);
SET @curso_ux := (SELECT id FROM curso WHERE titulo = 'Diseño UX/UI Profesional' LIMIT 1);
SET @curso_datos := (SELECT id FROM curso WHERE titulo = 'Python para Análisis de Datos' LIMIT 1);
SET @curso_cloud := (SELECT id FROM curso WHERE titulo = 'Arquitectura Cloud & DevOps' LIMIT 1);
SET @curso_marketing := (SELECT id FROM curso WHERE titulo = 'Growth Marketing Digital' LIMIT 1);

INSERT INTO leccion (id, curso_id, titulo, orden, youtube_id, duracion_min) VALUES
    (UUID(), @curso_web, 'Introducción a React', 1, 'SqcY0GlETPk', 45),
    (UUID(), @curso_web, 'Hooks, Estado y Efectos', 2, 'dQw4w9WgXcQ', 55),
    (UUID(), @curso_web, 'APIs con Node.js y Express', 3, 'dQw4w9WgXcQ', 70),

    (UUID(), @curso_ia, 'Qué es un LLM', 1, 'SqcY0GlETPk', 40),
    (UUID(), @curso_ia, 'Técnicas de prompting', 2, 'dQw4w9WgXcQ', 50),

    (UUID(), @curso_ux, 'Investigación con usuarios', 1, 'SqcY0GlETPk', 35),
    (UUID(), @curso_ux, 'Sistemas de diseño en Figma', 2, 'dQw4w9WgXcQ', 60),

    (UUID(), @curso_datos, 'Introducción a Pandas', 1, 'SqcY0GlETPk', 50),
    (UUID(), @curso_datos, 'Visualización de datos', 2, 'dQw4w9WgXcQ', 55),

    (UUID(), @curso_cloud, 'Contenedores con Docker', 1, 'SqcY0GlETPk', 65),
    (UUID(), @curso_cloud, 'Orquestación con Kubernetes', 2, 'dQw4w9WgXcQ', 75),

    (UUID(), @curso_marketing, 'Embudo de adquisición', 1, 'SqcY0GlETPk', 40),
    (UUID(), @curso_marketing, 'Métricas y experimentos', 2, 'dQw4w9WgXcQ', 45);
