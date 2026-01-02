# Nexo Vendas - Ponto de Venda e Gestão para Pequenos Comércios

<div align="center">

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Security](https://img.shields.io/badge/security-checked-blue)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)
![React](https://img.shields.io/badge/React-18-blue)

</div>

<p align="center">
  <a href="#sobre-o-projeto">Sobre</a> •
  <a href="#principais-funcionalidades">Funcionalidades</a> •
  <a href="#stack-de-tecnologias">Tecnologias</a> •
  <a href="#destaques-de-arquitetura-e-segurança">Arquitetura</a> •
  <a href="#como-executar-localmente">Executar</a>
</p>

<p align="center">
  <img src="https://github.com/PlaySx/Project-Acougue/graphs/languages" alt="Gráfico de Linguagens do Projeto" />
</p>

## Sobre o Projeto

**Nexo Vendas** é uma solução de software completa (Full Stack) projetada para modernizar a gestão de pequenos comércios, como açougues, mercearias e vendinhas. O sistema oferece um Ponto de Venda (PDV) ágil e um painel de controle (Dashboard) com indicadores de performance, permitindo que o proprietário tome decisões baseadas em dados e otimize suas operações diárias.

O projeto foi desenvolvido com uma arquitetura robusta e segura, utilizando as melhores práticas de mercado para garantir a integridade e a confidencialidade dos dados em um ambiente multi-tenant (multi-estabelecimentos).

---

### Demonstração

*É altamente recomendado gravar um GIF curto (usando ferramentas como LiceCap ou ScreenToGif) mostrando o fluxo principal: login, visualização do dashboard, criação de um pedido e visualização do perfil do cliente. E colocar o link aqui.*

`[Demonstração em GIF do sistema em ação]`

---

## Principais Funcionalidades

- **Autenticação Segura:** Sistema de login com JWT (JSON Web Tokens) e senhas criptografadas com BCrypt.
- **Arquitetura Multi-Tenant:** Múltiplos estabelecimentos podem usar o sistema de forma isolada e segura.
- **Controle de Acesso por Função (RBAC):** Perfis de `Proprietário` e `Funcionário` com permissões distintas.
- **Dashboard Dinâmico:** Visualização de KPIs (Faturamento, Pedidos, Ticket Médio) e gráficos de performance com filtros de data interativos.
- **Gestão de Clientes (CRM):**
  - Cadastro e edição de clientes.
  - **Visão 360° do Cliente:** Histórico completo de pedidos e KPIs individuais (Total Gasto, Última Compra).
  - **Importação Inteligente:** Cadastro de clientes em massa a partir de planilhas Excel (.xlsx).
- **Gestão de Produtos e Estoque:**
  - Cadastro de produtos por unidade ou por peso (kg).
  - O estoque é atualizado automaticamente a cada venda.
- **Ponto de Venda (PDV):**
  - Criação de pedidos com busca de clientes e produtos.
  - **Função "Repetir Pedido":** Recria um pedido antigo com preços e estoque atualizados.
  - **Cancelamento com Estorno:** Ao cancelar um pedido, os itens retornam automaticamente ao estoque.
- **Relatórios e Auditoria:**
  - **Exportação para Excel:** Todos os pedidos podem ser exportados para uma planilha.
  - Histórico de ações (em desenvolvimento).

---

## 🚀 Stack de Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3:** Framework principal para a construção da API REST.
- **Spring Security:** Para autenticação e autorização.
- **JPA / Hibernate:** Para persistência de dados.
- **Maven:** Gerenciador de dependências.
- **H2 Database:** Banco de dados em memória para o ambiente de desenvolvimento.
- **Apache POI:** Para manipulação de arquivos Excel (importação e exportação).

### Frontend
- **React 18**
- **React Router 6:** Para gerenciamento de rotas.
- **Material-UI (MUI):** Biblioteca de componentes para a interface.
- **Axios:** Cliente HTTP para comunicação com a API.
- **ApexCharts:** Para a criação dos gráficos do dashboard.
- **Context API:** Para gerenciamento de estado global (autenticação).

---

## 🛡️ Destaques de Arquitetura e Segurança

Esta seção detalha as decisões de engenharia tomadas para garantir que o sistema seja robusto, seguro e escalável.

1.  **Autenticação via JWT:** A comunicação entre frontend e backend é stateless. Após o login, o cliente recebe um token JWT que é usado para autenticar requisições subsequentes, seguindo o padrão da indústria para APIs REST.

2.  **Proteção contra IDOR (Insecure Direct Object Reference):** Este é um ponto crítico em sistemas multi-tenant. Foi implementada uma camada de autorização usando anotações `@PreAuthorize` e um `SecurityService` customizado. Isso garante que um usuário do "Açougue A" **não possa**, em hipótese alguma, visualizar ou modificar dados (clientes, produtos, pedidos) do "Açougue B", mesmo que ele tente manipular os IDs na URL.

3.  **Prevenção de XSS (Cross-Site Scripting):** Todos os dados de texto inseridos pelo usuário (nomes de produtos, observações, etc.) são sanitizados no backend usando a biblioteca **OWASP Java HTML Sanitizer**. Isso remove qualquer código HTML/JavaScript malicioso antes de salvar no banco de dados, prevenindo que scripts sejam executados no navegador de outros usuários.

4.  **Gerenciamento de Segredos:** Informações sensíveis, como a chave secreta do JWT, não estão "hardcoded". Elas são lidas a partir de **variáveis de ambiente**, uma prática recomendada para segurança em produção, evitando a exposição de segredos no código-fonte.

---

## ⚙️ Como Executar Localmente

**Pré-requisitos:**
- Java 17+
- Maven 3.8+
- Node.js 18+

```bash
# 1. Clone o repositório
git clone https://github.com/PlaySx/Project-Acougue.git
cd Project-Acougue

# 2. Execute o Backend
cd backend
mvn spring-boot:run

# 3. Em um novo terminal, execute o Frontend
cd ../frontend
npm install
npm start
```

- A API estará disponível em `http://localhost:8080`
- A aplicação React estará disponível em `http://localhost:3000` (ou outra porta, se a 3000 estiver em uso).

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
