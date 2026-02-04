FROM eclipse-temurin:21-jdk as build
WORKDIR /app
COPY . .
# Añade esta línea para dar permisos de ejecución al Maven Wrapper
RUN chmod +x ./mvnw
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar api-gestion.jar
EXPOSE 8080
CMD ["java", "-jar", "api-gestion.jar"]