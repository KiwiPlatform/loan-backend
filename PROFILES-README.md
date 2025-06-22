# 🚀 Guía de Perfiles Spring Boot - KiwiPay Loan Backend

## 📋 Estructura de Perfiles

### Archivos de Configuración

```
src/main/resources/
├── application.properties          # Configuración base (común a todos los entornos)
├── application-dev.properties      # Desarrollo
├── application-prod.properties     # Producción
└── application-test.properties     # Testing
```

## 🔧 Perfiles Disponibles

### 🛠️ **DEV (Desarrollo)** - `application-dev.properties`
- **Base de datos**: PostgreSQL local (`kiwipay_loan_dev`)
- **Puerto**: 8080
- **Logging**: Detallado (DEBUG)
- **CORS**: Permisivo (localhost)
- **Actuator**: Todos los endpoints expuestos
- **Seguridad**: Relajada para desarrollo

### 🚀 **PROD (Producción)** - `application-prod.properties`
- **Base de datos**: PostgreSQL con variables de entorno
- **Puerto**: Configurable via `SERVER_PORT`
- **Logging**: Optimizado (WARN/INFO)
- **CORS**: Restrictivo (dominios específicos)
- **Actuator**: Solo endpoints esenciales
- **Seguridad**: Estricta con HTTPS
- **Variables de entorno requeridas**:
  - `DB_PASSWORD`
  - `JWT_SECRET`
  - `CORS_ALLOWED_ORIGINS`

### ⚠️ **TESTING DESHABILITADO**
- **Tests eliminados**: Para evitar conflictos de configuración
- **Solo producción y desarrollo**: Enfoque simplificado sin tests

## 🎯 Cómo Activar Perfiles

### 1. **Por defecto** (configurado en `application.properties`)
```properties
spring.profiles.active=dev
```

### 2. **Línea de comandos**
```bash
# Desarrollo
java -jar -Dspring.profiles.active=dev kiwipay-loan-backend.jar

# Producción
java -jar -Dspring.profiles.active=prod kiwipay-loan-backend.jar

# Testing
java -jar -Dspring.profiles.active=test kiwipay-loan-backend.jar
```

### 3. **Gradle**
```bash
# Desarrollo (por defecto)
./gradlew bootRun

# Producción
./gradlew bootRun --args='--spring.profiles.active=prod'

# Testing
./gradlew bootRun --args='--spring.profiles.active=test'
```

### 4. **Variable de entorno**
```bash
# Windows
set SPRING_PROFILES_ACTIVE=prod

# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
```

### 5. **IntelliJ IDEA**
1. Edit Run Configuration
2. Program arguments: `--spring.profiles.active=dev`
3. O en Environment variables: `SPRING_PROFILES_ACTIVE=dev`

## 🔍 Verificar Perfil Activo

### Endpoint de Actuator
```bash
curl http://localhost:8080/actuator/env | grep "spring.profiles.active"
```

### Logs de aplicación
Busca en los logs:
```
Active profile: dev
```

## 📊 Comparación de Perfiles

| Característica | DEV | PROD |
|---------------|-----|------|
| Base de datos | PostgreSQL local | PostgreSQL remoto |
| Pool conexiones | 5 max | 20 max |
| Logging level | DEBUG | WARN/INFO |
| Actuator endpoints | Todos | Básicos |
| CORS | Permisivo | Restrictivo |
| Error details | Completos | Ocultos |
| SSL | No | Sí |

## 🛡️ Seguridad por Perfil

### Desarrollo
- JWT secret fijo (no seguro)
- Rate limiting relajado
- Errores detallados expuestos

### Producción
- JWT secret desde variable de entorno
- Rate limiting estricto
- Errores ocultos
- HTTPS obligatorio

### ⚠️ Testing Eliminado
- Sin configuración de tests
- Enfoque simplificado

## 🌍 Variables de Entorno para Producción

```bash
# Base de datos
DATABASE_URL=jdbc:postgresql://prod-server:5432/kiwipay_loan_prod
DB_USERNAME=kiwipay_user
DB_PASSWORD=super_secure_password

# Seguridad
JWT_SECRET=production-jwt-secret-minimum-256-bits-long
JWT_EXPIRATION=3600

# CORS
CORS_ALLOWED_ORIGINS=https://app.kiwipay.pe,https://admin.kiwipay.pe

# Rate Limiting
RATE_LIMIT_RPM=20
RATE_LIMIT_BURST=50

# SSL
SSL_KEYSTORE_PATH=/path/to/keystore.p12
SSL_KEYSTORE_PASSWORD=keystore_password

# Servidor
SERVER_PORT=8080
```

## 📝 Mejores Prácticas

1. **Nunca hardcodear secretos** en archivos de propiedades
2. **Usar variables de entorno** para configuración sensible
3. **Mantener configuración común** en `application.properties`
4. **Documentar variables requeridas** para cada entorno
5. **Validar configuración** antes de desplegar

## 🔧 Troubleshooting

### Problema: Perfil no se activa
**Solución**: Verificar orden de precedencia:
1. Argumentos de línea de comandos
2. Variables de entorno
3. `application.properties`

### Problema: Variables de entorno no se leen
**Solución**: Verificar sintaxis `${VARIABLE_NAME:default_value}`

### Problema: Configuración no se aplica
**Solución**: Verificar que el archivo `application-{profile}.properties` existe

## 📚 Referencias

- [Spring Boot Profiles Guide](https://medium.com/@patryk.sosinski/mastering-spring-boot-profiles-in-application-properties-c4e9ea46e994)
- [Spring Boot Official Documentation](https://docs.spring.io/spring-boot/reference/features/profiles.html)

## Resumen de Perfiles

### 🛠️ Desarrollo (dev)
- **Swagger**: ✅ Habilitado (requiere clave secreta)
- **Base de datos**: `kiwipay_loan_dev`
- **Logging**: DEBUG
- **Uso**: Desarrollo local con todas las herramientas

### 🧪 Staging
- **Swagger**: ✅ Habilitado (requiere clave secreta)
- **Base de datos**: `kiwipay_loan_staging`
- **Logging**: DEBUG
- **Uso**: Pruebas de integración y pre-producción

### 🚀 Producción (prod)
- **Swagger**: ❌ DESHABILITADO (por seguridad)
- **Base de datos**: `kiwipay_loan_prod`
- **Logging**: ERROR/WARN únicamente
- **Uso**: Entorno productivo con máxima seguridad y rendimiento

## Cómo Ejecutar en Cada Perfil

### Opción 1: Modificar application.properties
```properties
spring.profiles.active=dev    # o staging o prod
```

### Opción 2: Variable de entorno
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar kiwipay-loan-backend.jar
```

### Opción 3: Argumento de línea de comandos
```bash
java -jar kiwipay-loan-backend.jar --spring.profiles.active=prod
```

### Opción 4: En IntelliJ IDEA
1. Edit Configurations
2. En "Active profiles" escribir: `prod`
3. Run

## Probar con Postman en Producción

Como Swagger está deshabilitado en producción, usa Postman:

### 1. Endpoints disponibles:
```
GET    /api/v1/leads
GET    /api/v1/leads/{id}
POST   /api/v1/leads
PUT    /api/v1/leads/{id}
DELETE /api/v1/leads/{id}

GET    /api/v1/clinics
GET    /api/v1/specialties
```

### 2. Headers requeridos:
```
Content-Type: application/json
Accept: application/json
```

### 3. Ejemplo de request en Postman:

**Crear Lead:**
```
POST http://localhost:8080/api/v1/leads
Content-Type: application/json

{
  "clinicId": 1,
  "medicalSpecialtyId": 1,
  "clientName": "Juan Pérez",
  "clientDni": "12345678",
  "clientPhone": "999888777",
  "clientEmail": "juan@example.com",
  "estimatedAmount": 5000.00,
  "estimatedInstallments": 12,
  "observations": "Cliente interesado en cirugía"
}
```

## Diferencias Clave entre Perfiles

| Característica | Desarrollo | Staging | Producción |
|---------------|------------|---------|------------|
| **Swagger UI** | ✅ Con autenticación | ✅ Con autenticación | ❌ Deshabilitado |
| **API Docs** | ✅ Habilitado | ✅ Habilitado | ❌ Deshabilitado |
| **Actuator** | Todos los endpoints | Todos los endpoints | Solo health e info |
| **CORS** | Permisivo (localhost) | Permisivo (localhost) | Restrictivo |
| **Logging SQL** | ✅ Habilitado | ✅ Habilitado | ❌ Deshabilitado |
| **Error Details** | ✅ Completos | ✅ Completos | ❌ Ocultos |
| **Rate Limiting** | 100 req/min | 100 req/min | 60 req/min |
| **Cache** | ❌ Deshabilitado | ❌ Deshabilitado | ✅ Habilitado |
| **Compresión** | ✅ Habilitada | ✅ Habilitada | ✅ Optimizada |

## Configuración de Base de Datos

Todas las bases de datos usan las mismas credenciales por defecto:
```
Host: localhost
Port: 5432
Username: postgres
Password: root
```

Las bases de datos son:
- **Desarrollo**: `kiwipay_loan_dev`
- **Staging**: `kiwipay_loan_staging`
- **Producción**: `kiwipay_loan_prod`

## Seguridad en Producción

En producción se implementan las siguientes medidas:

1. **Swagger completamente deshabilitado**
2. **Logs mínimos** (solo ERROR y WARN)
3. **Sin detalles de errores** en respuestas
4. **Headers de seguridad** estrictos
5. **CORS restrictivo** (solo dominios autorizados)
6. **Rate limiting** más estricto
7. **Cache habilitado** para mejor rendimiento
8. **Compresión optimizada** de respuestas

## Recomendaciones

- **Desarrollo**: Usa este perfil para desarrollo local
- **Staging**: Usa para pruebas de integración
- **Producción**: 
  - Solo para el servidor de producción
  - Usa Postman o tu cliente HTTP preferido
  - Configura las variables de entorno apropiadas
  - Nunca expongas Swagger en producción

## Variables de Entorno Importantes

Para producción, configura estas variables:

```bash
# Base de datos (si no es localhost)
DATABASE_URL=jdbc:postgresql://prod-server:5432/kiwipay_loan_prod
DB_USERNAME=prod_user
DB_PASSWORD=secure_password

# JWT Secret (CAMBIAR EN PRODUCCIÓN)
JWT_SECRET=your-secure-jwt-secret-at-least-256-bits

# CORS (dominios permitidos)
CORS_ALLOWED_ORIGINS=https://app.kiwipay.pe,https://admin.kiwipay.pe

# Encriptación
KIWIPAY_ENCRYPTION_PASSWORD=your-encryption-password
KIWIPAY_ENCRYPTION_SALT=your-encryption-salt
``` 