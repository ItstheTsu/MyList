# MyList — Controle Financeiro Simples e Inteligente

O **MyList** é um aplicativo de controle financeiro pessoal criado para oferecer clareza e organização no seu dia a dia.  
A proposta é simples: você registra seus gastos e o sistema cuida de transformar esses dados em um painel fácil de entender, com alertas e histórico para ajudar nas suas decisões.

---

## 🚀 Funcionalidades

- Registro de gastos fixos e variáveis  
- Cálculo automático de:
  - Gastos mensais  
  - Gastos fixos  
  - Gastos variáveis  
  - Passivo para investimento  
  - Passivo pessoal  
  - Reserva de emergência  
  - Crédito usado e crédito restante  
  - FGTS  
  - Salário total  
- Comparativo financeiro dos últimos **12 meses**  
- Alertas inteligentes:
  - Gasto acima do previsto  
  - Investimento abaixo do ideal  
- Remoção automática de gastos fixos finalizados  
- Indicação de parcelas e boletos já pagos  
- Histórico contínuo a partir da data de criação da conta  

---

## 🛠️ Backend — Spring Boot

O backend do **MyList** é desenvolvido em **Spring Boot**, responsável por toda a lógica, cálculos e comunicação com o banco de dados MySQL.

Tecnologias e módulos principais:

- **Spring Web**: criação das APIs REST consumidas pelo frontend  
- **Spring Data JPA**: integração simples e eficiente com MySQL  
- **Spring Validation**: validação dos dados enviados pelo usuário  
- **Spring Security** (opcional): proteção de rotas e autenticação  
- Arquitetura organizada em camadas:  
  - `Controller` → recebe requisições  
  - `Service` → processa regras de negócio  
  - `Repository` → comunicação com o banco

Responsabilidades do backend:
- Processar cálculos mensais  
- Gerar alertas de uso e investimento  
- Atualizar status de parcelas e gastos fixos  
- Criar e manter um histórico mensal  
- Retornar dados de forma clara e otimizada para o frontend  

---

## 🎨 Frontend

O frontend é construído com **Vue.js** e **React**, garantindo uma interface rápida, responsiva e simples de navegar.

---

## 🗄️ Banco de Dados

Banco de dados em **MySQL**, estruturado para armazenar:

- Usuários  
- Gastos fixos  
- Gastos variáveis  
- Categorias  
- Alerta  
- Histórico mensal  
- Informações de crédito e salário  

---

## 🎯 Objetivo do Projeto

O **MyList** nasce com a missão de facilitar a vida financeira de qualquer pessoa, oferecendo uma plataforma clara, direta e eficiente para acompanhamento de gastos e evolução mensal.

Uma ferramenta simples, mas poderosa o suficiente para transformar o jeito que você lida com seu dinheiro.

---

[_Direitos reservados: Allan Correa, 2025_]
