# Usar una imagen de Java con Maven preinstalado
FROM eclipse-temurin:17-jdk

# Configurar JAVA_HOME manualmente
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos del proyecto
COPY . .

# Dar permisos de ejecución a Maven Wrapper
RUN chmod +x mvnw

# Verificar Java y Maven
RUN echo "JAVA_HOME is set to $JAVA_HOME"
RUN java -version
RUN mvn -version

# Construir la aplicación usando Maven sin `mvnw`
RUN mvn clean package -DskipTests

# Exponer el puerto de la aplicación
EXPOSE 8080

# Ejecutar la aplicación con el JAR generado
CMD ["java", "-jar", "target/basic-web-app-0.0.1-SNAPSHOT.jar"]
