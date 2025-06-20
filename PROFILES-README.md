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

### 🧪 **TEST (Testing)** - `application-test.properties`
- **Base de datos**: H2 en memoria
- **Puerto**: Aleatorio (0)
- **Logging**: Silencioso
- **CORS**: Permisivo para tests
- **Flyway**: Deshabilitado (usa create-drop)
- **H2 Console**: Habilitado para debugging

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

| Característica | DEV | PROD | TEST |
|---------------|-----|------|------|
| Base de datos | PostgreSQL local | PostgreSQL remoto | H2 memoria |
| Pool conexiones | 5 max | 20 max | Mínimo |
| Logging level | DEBUG | WARN/INFO | WARN |
| Actuator endpoints | Todos | Básicos | Mínimos |
| CORS | Permisivo | Restrictivo | Permisivo |
| Error details | Completos | Ocultos | Completos |
| SSL | No | Sí | No |

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

### Testing
- JWT secret fijo para tests
- Sin rate limiting
- Configuración permisiva

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