# Usar una imagen de Java con Maven preinstalado
FROM eclipse-temurin:17-jdk

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos del proyecto
COPY . .

# Dar permisos de ejecución a Maven Wrapper
RUN chmod +x mvnw

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto de la aplicación (ajusta si usas otro)
EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "target/*.jar"]
