# Etapa de build (compilação do projeto)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia todo o projeto para dentro do container
COPY . .

# Dá permissão de execução ao Maven Wrapper
RUN chmod +x ./mvnw

# Compila o projeto, ignorando testes
RUN ./mvnw clean package -DskipTests

# Etapa final (imagem para rodar a aplicação)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o .jar gerado do build anterior
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
