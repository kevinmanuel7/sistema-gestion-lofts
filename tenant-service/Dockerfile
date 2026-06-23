FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/*.jar app.jar
# Puerto del servicio (el 8085 según tus propiedades)
EXPOSE 8085
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]