# Etapa de build usando Gradle
FROM gradle:8-jdk17 AS build
WORKDIR /app

# Copiar archivos de configuración
COPY build.gradle settings.gradle ./

# Copiar código fuente
COPY src ./src

# Construir la aplicación (sin tests para optimizar)
RUN gradle build -x test

# Etapa de runtime - imagen ligera con JRE
FROM eclipse-temurin:17-jre-jammy

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR construido desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer puerto del servicio
EXPOSE 8082

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
