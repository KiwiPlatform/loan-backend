# KiwiPay Loan Backend

Sistema de gestión de leads para préstamos médicos/dentales de KiwiPay Perú.

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL**
- **Flyway** (Migraciones de BD)
- **MapStruct** (Mapeo de objetos)
- **OpenAPI/Swagger** (Documentación)
- **Lombok** (Reducción de boilerplate)

## 📋 Requisitos Previos

- JDK 17 o superior
- PostgreSQL 12 o superior
- Gradle 7.x o superior

## 🛠️ Configuración

### 1. Base de Datos

Crear una base de datos PostgreSQL:

```sql
CREATE DATABASE kiwipay_loan_dev;
```

### 2. Variables de Entorno

Configurar las siguientes variables de entorno o actualizar `application.yml`:

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=kiwipay_loan_dev
DB_USER=postgres
DB_PASSWORD=root
```

### 3. Ejecutar la Aplicación

```bash
# Desarrollo
./gradlew bootRun

# Con perfil específico
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 📚 Documentación API

Una vez iniciada la aplicación, la documentación Swagger está disponible en:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🔧 Endpoints Principales

### Leads
- `POST /api/v1/leads` - Crear nuevo lead
- `GET /api/v1/leads` - Listar leads con paginación y filtros
- `GET /api/v1/leads/{id}` - Obtener detalle de un lead
- `PATCH /api/v1/leads/{id}/status` - Actualizar estado de un lead

### Catálogos
- `GET /api/v1/clinics` - Listar clínicas activas
- `GET /api/v1/medical-specialties` - Listar especialidades médicas

## 📊 Modelo de Datos

### Lead
- Información del recepcionista
- Información del cliente
- Datos financieros (ingreso mensual, costo tratamiento)
- Clínica y especialidad médica
- Estado del lead
- Timestamps de auditoría

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas:

```
├── controller/     # Controladores REST
├── service/        # Lógica de negocio
├── repository/     # Acceso a datos
├── entity/         # Entidades JPA
├── dto/           # Objetos de transferencia
├── mapper/        # Mapeo entre DTOs y entidades
├── exception/     # Manejo de excepciones
└── config/        # Configuraciones
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar con cobertura
./gradlew test jacocoTestReport
```

## 📦 Build y Despliegue

```bash
# Generar JAR
./gradlew clean build

# Ejecutar JAR
java -jar build/libs/kiwipay-loan-backend-0.0.1-SNAPSHOT.jar
```

## 🔒 Seguridad

- CORS configurado para ambientes específicos
- Validación exhaustiva de entrada de datos
- Manejo centralizado de errores
- Logs detallados para auditoría

## 👥 Equipo

Desarrollado por el equipo de KiwiPay Perú.

## 📄 Licencia

Propiedad de KiwiPay Perú. Todos los derechos reservados. 