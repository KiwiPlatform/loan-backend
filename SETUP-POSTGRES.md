# 🗄️ Configuración PostgreSQL Local para KiwiPay-Loan-Backend

## 📋 Pasos de instalación

### 1. **Instalar PostgreSQL**
- Ve a: https://www.postgresql.org/download/windows/
- Descarga e instala PostgreSQL 16 (o la versión más reciente)
- **IMPORTANTE**: Durante la instalación:
  - Usuario: `postgres`
  - **Contraseña: `root`** (la misma que está en tu configuración)
  - Puerto: `5432`

### 2. **Configurar el proyecto**
Ejecuta estos scripts en orden:

```batch
# 1. Iniciar PostgreSQL (si no está iniciado)
start-postgres-service.bat

# 2. Crear la base de datos del proyecto
create-database.bat

# 3. Verificar que todo esté correcto
check-postgres.bat

# 4. Ejecutar la aplicación Spring Boot
run-app.bat
```

## 🚀 Acceso rápido

### Ejecutar la aplicación:
```batch
run-app.bat
```

### Verificar estado:
```batch
check-postgres.bat
```

### Acceder a Swagger:
```
http://localhost:8080/swagger-ui.html
```

### Usar pgAdmin (interfaz gráfica):
- Abrir pgAdmin desde el menú de inicio
- Conectar con:
  - Host: `localhost`
  - Puerto: `5432`
  - Usuario: `postgres`
  - Contraseña: `root`
  - Base de datos: `kiwipay_loan_dev`

## 🔧 Configuración actual

Tu aplicación está configurada para usar:
- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: kiwipay_loan_dev
- **Usuario**: postgres
- **Contraseña**: root

## ✅ ¿Todo funcionando?

Si ves este mensaje al ejecutar `run-app.bat`:
```
✅ PostgreSQL está ejecutándose
🏗️ Compilando y ejecutando aplicación...
```

¡Entonces todo está bien! Ve a http://localhost:8080/swagger-ui.html

## 🆘 Problemas comunes

- **PostgreSQL no inicia**: Ejecuta `start-postgres-service.bat` como administrador
- **Base de datos no existe**: Ejecuta `create-database.bat`
- **Error de contraseña**: Verifica que usaste `root` como contraseña durante la instalación 