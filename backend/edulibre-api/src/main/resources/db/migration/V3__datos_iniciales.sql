-- Datos iniciales: categorías del catálogo y usuario administrador.
-- Credenciales del admin: admin@edulibre.com / admin123

INSERT INTO categoria (id, nombre, slug) VALUES
    (UUID(), 'Desarrollo Web', 'desarrollo-web'),
    (UUID(), 'Inteligencia Artificial', 'inteligencia-artificial'),
    (UUID(), 'Diseño UX/UI', 'diseno-ux-ui'),
    (UUID(), 'Marketing Digital', 'marketing-digital'),
    (UUID(), 'Ciencia de Datos', 'ciencia-de-datos');

INSERT INTO usuario (id, nombre, email, password, rol) VALUES
    (UUID(), 'Administrador', 'admin@edulibre.com',
     '$2b$10$DxOTaaQp.edaWzZodcj7UOrfoscgQVyqK2sQy6.HJf2YE0Aep4Oh.', 'ADMIN');
