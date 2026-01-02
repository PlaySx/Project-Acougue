# 🥩 Nexo Vendas - Sistema de Gestão para Varejo

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![Material UI](https://img.shields.io/badge/Material--UI-5.0-007FFF?style=for-the-badge&logo=mui&logoColor=white)
![Security](https://img.shields.io/badge/Spring_Security-6.0-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)

> Um sistema Full Stack robusto para gestão de pequenos comércios (açougues, mercadinhos, padarias), focado em controle de estoque, fluxo de vendas ágil e inteligência de cliente (CRM).

---

## 🚀 Sobre o Projeto

Este projeto foi desenvolvido para resolver dores reais do pequeno varejo: a dificuldade de gerenciar estoque perecível, a lentidão no atendimento e a falta de dados para tomada de decisão.

Diferente de CRUDs simples, o **Nexo Vendas** implementa regras de negócio complexas, como estorno automático de estoque em cancelamentos, precificação dinâmica por peso (kg) ou unidade, e um sistema de segurança blindado contra vulnerabilidades comuns (IDOR, XSS).

## 🎯 Funcionalidades Principais

### 💼 Gestão & Dashboard
- **Dashboard Analítico:** KPIs em tempo real (Faturamento, Ticket Médio, Novos Clientes) e gráficos de desempenho com filtro de data dinâmico.
- **Relatórios:** Exportação de listagens de pedidos para Excel (.xlsx) para contabilidade e análise externa.

### 🛒 Vendas & Estoque
- **PDV Ágil:** Criação de pedidos otimizada com busca inteligente de clientes e produtos.
- **Controle de Estoque:** Baixa automática na venda e **estorno automático** em caso de cancelamento.
- **Repetir Pedido:** Funcionalidade "One-Click" para clonar pedidos anteriores com atualização automática de preços e validação de estoque atual.
- **Precificação Híbrida:** Suporte nativo para produtos vendidos por peso (gramas/kg) e por unidade.

### 👥 CRM (Gestão de Clientes)
- **Visão 360°:** Perfil detalhado do cliente com histórico completo de compras e total gasto (LTV).
- **Importação em Massa:** Ferramenta para importar planilhas de clientes legados com validação de dados e tratamento de erros.
- **Venda Ativa:** Início de pedido diretamente do perfil do cliente.

### 🔒 Segurança & Arquitetura
- **Autenticação:** Login seguro com JWT (JSON Web Token).
- **RBAC (Role-Based Access Control):** Permissões distintas para Proprietários (Owner) e Funcionários (Employee).
- **Proteção Avançada:**
  - **Anti-IDOR:** Blindagem de controllers garantindo que usuários só acessem dados do seu próprio estabelecimento.
  - **Sanitização:** Proteção contra XSS (Cross-Site Scripting) em todos os inputs de texto.
  - **Segredos:** Gerenciamento de chaves sensíveis via variáveis de ambiente.

---

## 🛠️ Tecnologias Utilizadas

### Backend (API RESTful)
- **Java 17** & **Spring Boot 3**
- **Spring Security 6** (Autenticação & Autorização)
- **Spring Data JPA** (Persistência)
- **H2 Database** (Ambiente de Dev) / Preparado para PostgreSQL
- **Apache POI** (Geração e Leitura de Excel)
- **Bean Validation** (Integridade de dados)
- **OWASP Java HTML Sanitizer** (Segurança)

### Frontend (SPA)
- **React.js**
- **Material-UI (MUI)** (Design System)
- **Axios** (Comunicação HTTP)
- **React Router DOM** (Navegação)
- **ApexCharts** (Visualização de Dados)
- **Date-fns** (Manipulação de Datas)

---

## 🏗️ Arquitetura e Padrões

O projeto segue uma arquitetura em camadas limpa e escalável:

1.  **Controllers:** Responsáveis apenas pela interface HTTP e validação básica.
2.  **Services:** Contêm toda a lógica de negócio (ex: cálculo de preços, regras de estorno, orquestração de cadastro).
3.  **Repositories:** Camada de acesso a dados.
4.  **DTOs (Data Transfer Objects):** Desacoplamento entre a API e as Entidades do banco, garantindo segurança e flexibilidade.
5.  **Mappers:** Conversão eficiente entre Entidades e DTOs.

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
- Java 17+
- Node.js 16+
- Maven

### 1. Backend
```bash
cd backend
# O sistema usa H2 (banco em memória) por padrão, sem necessidade de configuração extra.
mvn spring-boot:run
```
O servidor iniciará em `http://localhost:8080`.

### 2. Frontend
```bash
cd frontend
npm install
npm start
```
A aplicação abrirá em `http://localhost:3000`.

### 3. Acesso Inicial
Crie uma conta na tela de registro selecionando a opção **"Sou Proprietário"**. O sistema criará automaticamente seu estabelecimento e seu usuário administrativo.

Desenvolvido com 💻 e ☕ por **Gabriel Lopes Lima**.
