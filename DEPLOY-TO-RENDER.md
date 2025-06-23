# Guía de Despliegue en Render - ENTORNO DESARROLLO (DEV)

## Archivos Creados

1. **Dockerfile** - Configuración de Docker optimizada para Spring Boot (perfil dev)
2. **render-env-variables.txt** - Todas las variables de entorno para desarrollo con credenciales reales
3. **.dockerignore** - Archivos excluidos del contexto de Docker
4. **.gitignore** actualizado - Incluye archivos sensibles

## Pasos para Desplegar en Render - DESARROLLO

### 1. Preparación Local

✅ **Las credenciales ya están configuradas** en `render-env-variables.txt` con:
- Base de datos PostgreSQL de Render (ya creada)
- Claves de encriptación específicas para desarrollo
- Configuración JWT para desarrollo
- Swagger secrets para desarrollo

### 2. Subir Código a GitHub

1. Commit y push de los cambios:
   ```bash
   git add Dockerfile .dockerignore DEPLOY-TO-RENDER.md render-env-variables.txt
   git commit -m "Add Render deployment configuration for DEV environment"
   git push origin feature/add-authentication
   ```

### 3. Crear Cuenta en Render

1. Ve a [https://render.com](https://render.com)
2. Regístrate con tu cuenta de GitHub

### 4. Base de Datos PostgreSQL

✅ **Ya tienes la base de datos creada** con estas credenciales:
- **Database**: `kiwipay_dev_db`
- **Username**: `kiwi_platform_dev_user`
- **Host**: `dpg-d1cniire5dus73aeb4vg-a.oregon-postgres.render.com`
- **Port**: `5432`
- **Internal URL**: Ya configurada en `render-env-variables.txt`

### 5. Crear Web Service

1. En el Dashboard, click en "New +"
2. Selecciona "Web Service"
3. Conecta tu repositorio de GitHub
4. Configura:
   - Name: `kiwipay-loan-backend-dev`
   - Runtime: Docker
   - Branch: `feature/add-authentication`
   - Region: Oregon (misma que tu base de datos)
   - Plan: Free (ideal para desarrollo) o Starter

### 6. Configurar Variables de Entorno

En la sección "Environment Variables" del servicio:

1. Click en "Add Environment Variable" para cada variable
2. **Copia EXACTAMENTE** las variables del archivo `render-env-variables.txt`
3. **Variables ya configuradas listas para usar:**

   ```
   SPRING_PROFILES_ACTIVE=dev
   
   # Base de datos (credenciales reales de tu BD en Render)
   DATABASE_URL=postgresql://kiwi_platform_dev_user:IxD3LmtH7kKtuikxmdyX9254OxVcoIcr@dpg-d1cniire5dus73aeb4vg-a.oregon-postgres.render.com/kiwipay_dev_db
   DB_USERNAME=kiwi_platform_dev_user
   DB_PASSWORD=IxD3LmtH7kKtuikxmdyX9254OxVcoIcr
   
   # Pool de conexiones para desarrollo
   DB_POOL_SIZE=5
   DB_MIN_IDLE=2
   DB_TIMEOUT=30000
   
   # JWT para desarrollo
   JWT_SECRET=ZGV2U2VjcmV0S2V5Rm9yS2l3aVBheUxvYW5CYWNrZW5kRGV2ZWxvcG1lbnQyNTZCaXRz
   JWT_EXPIRATION=86400
   JWT_REFRESH_EXPIRATION=604800
   
   # Encriptación (claves específicas que proporcionaste)
   KIWIPAY_ENCRYPTION_PASSWORD=a1b2c3d4e5f6789012345678901234567890abcdef123456789abcdef0123456
   KIWIPAY_ENCRYPTION_SALT=fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321
   
   # Swagger para desarrollo
   SWAGGER_DEV_SECRET=e4d91c7a8f3b5d6e2a1c9b8d7f6e5a4c3b2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f
   SWAGGER_STAGING_SECRET=7b6a5d4c3f2e1d0b9a8c7f6e5d4c3b2a1f0e9d8c7b6a5f4e3d2c1b0a9f8e7d6
   
   # CORS permisivo para desarrollo
   CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001,http://localhost:4200,http://localhost:8080,http://localhost:8081,https://tu-app.onrender.com
   
   # Rate limiting más permisivo para desarrollo
   RATE_LIMIT_RPM=100
   RATE_LIMIT_BURST=200
   ```

   **📋 TODAS las variables están en `render-env-variables.txt` - solo copia y pega**

### 7. Configurar Health Check

1. En "Health Check Path": `/actuator/health`
2. Auto-Deploy: ON (para despliegue automático)

### 8. Desplegar

1. Click en "Create Web Service"
2. Render comenzará a construir y desplegar tu aplicación
3. Puedes ver los logs en tiempo real

### 9. Verificar Despliegue

Una vez desplegado, verifica:

1. **Health Check**: `https://tu-servicio.onrender.com/actuator/health`
   - Deberías ver: `{"status":"UP"}`

2. **Swagger UI**: `https://tu-servicio.onrender.com/swagger-ui.html`
   - ✅ **Swagger está HABILITADO en desarrollo**
   - Podrás probar todos los endpoints de la API

3. **API Base**: `https://tu-servicio.onrender.com/api/v1`

## Características del Entorno de Desarrollo

✅ **Configuración DEV incluye:**
- **Swagger UI habilitado** - Documentación interactiva de la API
- **Logging detallado** - Para debugging y desarrollo
- **CORS permisivo** - Permite conexiones desde localhost
- **Rate limiting relajado** - 100 requests/minuto vs 60 en producción
- **JWT con mayor duración** - 24 horas vs 1 hora en producción
- **Base de datos PostgreSQL en Render** - Ya configurada y conectada
- **Configuración de pool pequeña** - Optimizada para desarrollo

## Comandos Útiles

### Ver logs en tiempo real
En el Dashboard de Render, ve a tu servicio y click en "Logs"

### Redeploy manual
En el Dashboard, click en "Manual Deploy" > "Deploy latest commit"

### Conectar a la BD (si usas Render PostgreSQL)
```bash
psql "postgresql://usuario:password@host:5432/database"
```

## Solución de Problemas

1. **Error de puerto**: Render asigna el puerto automáticamente vía variable PORT
2. **Error de BD**: Verifica que DATABASE_URL esté correcta
3. **Error de memoria**: Considera actualizar el plan si hay OOM
4. **Error de build**: Revisa los logs de build en el Dashboard

## Notas de Seguridad para Desarrollo

- ⚠️ **Las claves proporcionadas son para DESARROLLO únicamente**
- ⚠️ **NO uses estas claves en producción**
- ✅ **Swagger está habilitado para facilitar el desarrollo**
- ✅ **CORS es permisivo para desarrollo local**
- ✅ **Logging detallado para debugging**
- 🔄 **Cambia todas las claves para producción**

## Próximos Pasos para Desarrollo

1. **Probar la API** usando Swagger UI
2. **Conectar frontend** a la URL de Render
3. **Revisar logs** para debugging
4. **Configurar entorno de producción** cuando esté listo
5. **Hacer backup de la base de datos** antes de cambios importantes

## Conexión desde el Frontend

Si tu frontend está en desarrollo local, usa:
```javascript
// Configuración de API para desarrollo
const API_BASE_URL = 'https://tu-servicio.onrender.com/api/v1';
```

## Comando para Conectar a la Base de Datos

```bash
PGPASSWORD=IxD3LmtH7kKtuikxmdyX9254OxVcoIcr psql -h dpg-d1cniire5dus73aeb4vg-a.oregon-postgres.render.com -U kiwi_platform_dev_user kiwipay_dev_db
``` 