# 🚨 CONFIGURACIÓN URGENTE DE VARIABLES DE ENTORNO EN RENDER

## Variables CRÍTICAS para configurar AHORA:

Ve a tu servicio en Render Dashboard → **Environment** → **Add Environment Variable**

### 1. Variables de Base de Datos (CRÍTICAS)
```
DATABASE_URL=postgresql://kiwi_platform_dev_user:IxD3LmtH7kKtuikxmdyX9254OxVcoIcr@dpg-d1cniire5dus73aeb4vg-a.oregon-postgres.render.com/kiwipay_dev_db

SPRING_PROFILES_ACTIVE=dev
```

### 2. Variables de DataSource (Adicionales)
```
DB_USERNAME=kiwi_platform_dev_user
DB_PASSWORD=IxD3LmtH7kKtuikxmdyX9254OxVcoIcr
DB_POOL_SIZE=5
DB_MIN_IDLE=2
DB_TIMEOUT=30000
```

### 3. Variables de Seguridad
```
JWT_SECRET=ZGV2U2VjcmV0S2V5Rm9yS2l3aVBheUxvYW5CYWNrZW5kRGV2ZWxvcG1lbnQyNTZCaXRz
JWT_EXPIRATION=86400
JWT_REFRESH_EXPIRATION=604800

KIWIPAY_ENCRYPTION_PASSWORD=a1b2c3d4e5f6789012345678901234567890abcdef123456789abcdef0123456
KIWIPAY_ENCRYPTION_SALT=fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321
KIWIPAY_ENCRYPTION_ALGORITHM=AES
KIWIPAY_ENCRYPTION_KEY_LENGTH=256
KIWIPAY_ENCRYPTION_MODE=GCM
KIWIPAY_ENCRYPTION_PADDING=NoPadding
KIWIPAY_ENCRYPTION_PROVIDER=SunJCE
KIWIPAY_ENCRYPTION_ITERATIONS=100000
```

### 4. Variables de Swagger
```
SWAGGER_DEV_SECRET=e4d91c7a8f3b5d6e2a1c9b8d7f6e5a4c3b2d1e0f9a8b7c6d5e4f3a2b1c0d9e8f
SWAGGER_STAGING_SECRET=7b6a5d4c3f2e1d0b9a8c7f6e5d4c3b2a1f0e9d8c7b6a5f4e3d2c1b0a9f8e7d6
```

### 5. Variables de CORS
```
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001,http://localhost:4200,http://localhost:8080,http://localhost:8081,https://tu-app.onrender.com
```

### 6. Variables de Rate Limiting
```
RATE_LIMIT_RPM=100
RATE_LIMIT_BURST=200
```

### 7. Variables de Sistema
```
LOG_PATH=/var/log/kiwipay
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB
PROMETHEUS_ENABLED=false
```

## 🚀 Después de agregar las variables:

1. **Hacer redeploy**: En Render Dashboard → **Manual Deploy** → **Deploy latest commit**
2. **Verificar logs**: Debe conectarse a la base de datos exitosamente
3. **Probar health check**: `https://tu-app.onrender.com/actuator/health`

## ⚠️ NOTA IMPORTANTE:

Si el error persiste después de configurar las variables, también puedes probar temporalmente agregar esta variable para debug:

```
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=DEBUG
```

Esto te dará más información sobre qué está fallando en la conexión. 