# ----------------------------------------------------------------
# Dockerfile para o Frontend (React)
# ----------------------------------------------------------------

# Etapa 1: Construção (Build Stage)
# Usamos uma imagem do Node.js. A versão "alpine" é extra leve.
# Damos o apelido "build" para esta etapa.
FROM node:18-alpine AS build

WORKDIR /app

# Copia o package.json e o package-lock.json.
# Mesma otimização do backend: se estes arquivos não mudam, o cache é usado.
COPY package*.json ./

# Instala todas as dependências do projeto.
RUN npm install

# Agora, copia o resto do código-fonte (componentes, páginas, etc.).
COPY . .

# Executa o script de build do React.
# Isso cria uma pasta /app/build com a versão otimizada e minificada do seu site.
RUN npm run build

# ---

# Etapa 2: Servidor (Serve Stage)
# Começamos do zero com uma imagem de um servidor web Nginx, que é
# extremamente leve e performático para servir arquivos estáticos.
FROM nginx:stable-alpine

# Copia APENAS a pasta "build" gerada na etapa anterior para a pasta
# padrão do Nginx que serve o conteúdo web.
COPY --from=build /app/build /usr/share/nginx/html

# Informa ao Docker que o Nginx, por padrão, usa a porta 80.
EXPOSE 80

# O Nginx já é configurado para iniciar automaticamente.
# Não precisamos de um comando CMD ou ENTRYPOINT aqui.