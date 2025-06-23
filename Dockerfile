# Etapa de construcción
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app

# Copiar archivos de gradle
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Dar permisos de ejecución a gradlew y descargar dependencias
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN ./gradlew build -x test --no-daemon

# Etapa de producción
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar dumb-init para manejar señales correctamente
RUN apk add --no-cache dumb-init

# Crear usuario no-root
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar al usuario no-root
USER appuser

# Puerto por defecto (Render lo sobrescribe con la variable PORT)
EXPOSE 8080

# Usar dumb-init para ejecutar la aplicación
ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "-Dserver.port=${PORT:-8080}", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}", "app.jar"] 