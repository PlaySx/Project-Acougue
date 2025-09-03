# ================= STAGE 1: Build =================
# Usa uma imagem base com Maven e JDK 17 para compilar o projeto.
# "builder" é um nome que damos para esta etapa.
FROM maven:3.8.5-openjdk-17 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache de camadas do Docker.
# Se as dependências não mudarem, o Docker não vai baixá-las de novo.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o resto do código-fonte da sua aplicação
COPY src ./src

# Compila a aplicação e gera o arquivo .jar, pulando os testes.
RUN mvn package -DskipTests

# ================= STAGE 2: Run =================
# Usa uma imagem base leve, apenas com o Java Runtime (JRE), para rodar a aplicação.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copia o arquivo .jar gerado na etapa de build para a imagem final.
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado.
ENTRYPOINT ["java", "-jar", "app.jar"]