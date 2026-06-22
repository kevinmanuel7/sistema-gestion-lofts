# 1. Imagen base oficial de Java 17 (ligera y rápida)
FROM eclipse-temurin:17-jdk-alpine

# 2. Carpeta de trabajo dentro del contenedor
WORKDIR /app

# 3. Copiar el archivo .jar de tu carpeta target al contenedor renombrándolo como app.jar
COPY target/*.jar app.jar

# 4. Exponer el puerto en el que corre tu Utility (8082)
EXPOSE 8082

# 5. Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]