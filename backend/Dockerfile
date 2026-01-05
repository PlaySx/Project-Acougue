# ----------------------------------------------------------------
# Dockerfile para o Backend (Spring Boot)
# ----------------------------------------------------------------

# Etapa 1: Construção (Build Stage)
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml
COPY pom.xml .

# Baixa dependências
RUN mvn dependency:go-offline

# Copia o código
COPY src ./src

# Compila e gera o JAR (pulando testes)
RUN mvn clean package -Dmaven.test.skip=true

# ---

# Etapa 2: Execução (Run Stage)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta
EXPOSE 8080

# Comando de inicialização
# CORREÇÃO: Ativando o perfil 'production' do Spring
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=production"]
