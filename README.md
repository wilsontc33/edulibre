# EduLibre

Plataforma de cursos: API Spring Boot + frontend Angular.

## Requisitos

- Java 21
- MySQL 8 (corriendo en `localhost:3306`)
- Node.js LTS
- pnpm (se activa con Corepack)

## 1. Backend

```bash
cd backend/edulibre-api
./gradlew bootRun
```

Disponible en `http://localhost:8080`. Las tablas y datos iniciales se crean solos con Flyway.


## 2. Frontend

```bash
cd frontend/edulibre-web
corepack enable
pnpm install
pnpm start
```

Abrir `http://localhost:4200`. El proxy ya redirige `/api` al backend.

## Credenciales de prueba

- **Admin:** `admin@edulibre.com` / `admin123`

## Notas

- En Windows usar `gradlew.bat bootRun` y definir las variables con `set` o PowerShell.
