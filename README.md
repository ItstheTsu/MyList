# MyList — Controle Financeiro Pessoal

O **MyList** é um aplicativo de controle financeiro pessoal que oferece clareza e organização no dia a dia.  
Ele permite registrar gastos, acompanhar alertas inteligentes e analisar o histórico financeiro de forma simples e intuitiva.

---

## 🚀 Funcionalidades

- Registro de gastos fixos e variáveis  
- Cálculo automático de:
  - Gastos mensais, fixos e variáveis  
  - Passivo para investimento e passivo pessoal  
  - Reserva de emergência  
  - Crédito usado e restante  
  - FGTS e salário total  
- Comparativo financeiro dos últimos **12 meses**  
- Alertas inteligentes:
  - Gasto acima do previsto  
  - Investimento abaixo do ideal  
- Remoção automática de gastos fixos finalizados  
- Indicação de parcelas e boletos pagos  
- Histórico contínuo a partir da criação da conta  

---

## 🛠️ Backend — Spring Boot

O backend é responsável por toda a lógica, cálculos e comunicação com o banco de dados MySQL.

### Tecnologias e módulos
- **Spring Web** — APIs REST  
- **Spring Data JPA** — integração com MySQL  
- **Spring Validation** — validação de dados  
- **Spring Security** — proteção de rotas e autenticação (JWT)  

### Arquitetura do Backend

O backend do **MyList** segue o padrão **camadas separadas** (Layered Architecture), garantindo organização, manutenção e escalabilidade.

#### Estrutura de Pacotes

- **config**  
  - Configurações gerais da aplicação  
  - Ex.: CORS, beans, segurança (SecurityConfig)  

- **controller**  
  - Recebe requisições HTTP do frontend  
  - Valida dados de entrada e envia respostas JSON  
  - Ex.: `UserController`, `AuthController`, `ExpenseController`

- **dto**  
  - Objetos de transferência de dados (Data Transfer Objects)  
  - Usados para receber ou enviar dados de forma segura sem expor entidades  
  - Ex.: `LoginRequest`, `LoginResponse`, `PaidStatusRequest`

- **entity**  
  - Classes que representam tabelas do banco de dados  
  - Mapeamento JPA/Hibernate  
  - Ex.: `User`, `Expense`, `ExpenseType`

- **repository**  
  - Responsável pelo acesso ao banco de dados  
  - CRUD e queries personalizadas usando Spring Data JPA  
  - Ex.: `UserRepository`, `ExpenseRepository`

- **security**  
  - Autenticação e autorização de usuários  
  - JWT para validação de tokens  
  - Filtros para proteger rotas  
  - Ex.: `JwtFilter`, `JwtUtil`, `MyUserDetailsService`  

- **service**  
  - Contém toda a **lógica de negócio**  
  - Processa cálculos financeiros, alertas, histórico e interações entre múltiplos repositórios  
  - Ex.: `UserService`, `ExpenseService`  

#### Fluxo de Requisição

1. O **frontend** faz uma requisição HTTP para o backend.  
2. O **Controller** recebe a requisição e realiza validações iniciais.  
3. O **Service** processa a lógica de negócio, cálculos e regras financeiras.  
4. O **Repository** acessa o banco de dados MySQL e retorna os dados.  
5. O **Controller** envia a resposta final ao frontend.  
6. A camada **Security** garante que apenas usuários autenticados acessem rotas protegidas.  

> Essa organização garante que cada camada tenha **uma única responsabilidade**, tornando o sistema modular, seguro e fácil de evoluir.

### Responsabilidades
- Processar cálculos e alertas mensais  
- Atualizar status de parcelas e gastos  
- Criar e manter histórico financeiro  
- Retornar dados otimizados para o frontend  

---

## 🎨 Frontend

- Desenvolvido com **Vue.js**  
- Interface rápida, responsiva e amigável  

---

## 🗄️ Banco de Dados

- MySQL  
- Estruturado para armazenar:
  - Usuários  
  - Gastos fixos e variáveis  
  - Categorias  
  - Alertas  
  - Histórico mensal  
  - Informações de crédito e salário  

---

## 🎯 Objetivo

O **MyList** facilita a gestão financeira pessoal, oferecendo uma plataforma clara e eficiente para acompanhar gastos, investimentos e evolução mensal.

---

*Direitos reservados: Allan Correa, 2025*