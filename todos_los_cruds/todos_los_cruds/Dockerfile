# Usar OpenJDK 21 para la construcción
FROM eclipse-temurin:21-jdk AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar solo los archivos necesarios para descargar dependencias primero (mejor caching)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests -B

# Etapa de producción con JRE más ligero
FROM eclipse-temurin:21-jre

# Crear usuario no-root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Cambiar ownership del archivo
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring

# Exponer puerto
EXPOSE 8080

# Configurar JVM para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]