FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/inventory-service-0.0.1-SNAPSHOT.jar app.jar
# Puerto en el que corre este servicio
EXPOSE 8081
#Comando para ejecutar la aplicación
ENTRYPOINT ["javad", "-jar", "app.jar"]