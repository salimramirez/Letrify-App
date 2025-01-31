# Usar una imagen de Java con Maven preinstalado
FROM eclipse-temurin:17-jdk

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos del proyecto
COPY . .

# Dar permisos de ejecución a Maven Wrapper
RUN chmod +x mvnw

# Resolver dependencias ANTES de la compilación
RUN ./mvnw dependency:resolve

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto de la aplicación
EXPOSE 8080

# Ejecutar la aplicación con el JAR generado
CMD ["java", "-jar", "target/basic-web-app-0.0.1-SNAPSHOT.jar"]
