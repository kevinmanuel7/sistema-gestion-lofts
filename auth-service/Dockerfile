# Usa una imagen ligera de Java 17
FROM eclipse-temurin:17-jdk-jammy

# Directorio interno del contenedor
WORKDIR /app

# Copia el archivo compilado al contenedor
COPY target/*.jar app.jar

# Expone el puerto privado
EXPOSE 8081

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]