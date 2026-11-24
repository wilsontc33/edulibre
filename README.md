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

## 3. Notificaciones por email

Al aprobar o rechazar una postulación, el backend envía un correo HTML al postulante (solo el resultado, no el acuse de recibo). Se usa Spring Boot Mail con el relay SMTP de [Brevo](https://www.brevo.com/) y plantillas Thymeleaf.

Variables de entorno del backend:

| Variable | Descripción | Ejemplo |
| --- | --- | --- |
| `MAIL_HOST` | Host SMTP | `smtp-relay.brevo.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Usuario/login SMTP de Brevo | `tu-login@smtp-brevo.com` |
| `MAIL_PASSWORD` | SMTP key de Brevo (no la contraseña de la cuenta) | `xsmtpsib-...` |
| `MAIL_FROM` | Remitente validado en Brevo | `no-reply@edulibre.com` |
| `MAIL_FROM_NAME` | Nombre visible del remitente | `EduLibre` |
| `FRONTEND_URL` | URL usada en los enlaces del correo | `http://localhost:4200` |

Sin `MAIL_USERNAME` y `MAIL_PASSWORD` el intento de envío fallará y se registrará en el log, sin revertir la aprobación/rechazo. Si no necesitas correos en desarrollo, puedes dejar esas variables vacías.

## Credenciales de prueba

- **Admin:** `admin@edulibre.com` / `admin123`

## Notas

- En Windows usar `gradlew.bat bootRun` y definir las variables con `set` o PowerShell.
