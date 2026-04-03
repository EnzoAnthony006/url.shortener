# 🔗 URL Shortener API

API REST para encurtamento de URLs, desenvolvida com Java e Spring Boot. O projeto utiliza Redis como cache para otimizar o desempenho nos redirecionamentos, autenticação JWT, containerização com Docker e cobertura de testes com JUnit 5 e Mockito.

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.4.4
- Spring Security + JWT
- Spring Data JPA
- Spring Data Redis
- MySQL 8
- Docker + Docker Compose
- JUnit 5 + Mockito
- Swagger / OpenAPI 3
- Maven

## ⚙️ Como rodar o projeto

### Pré-requisitos

- Docker e Docker Compose instalados
- MySQL rodando localmente na porta 3306

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/EnzoAnthony006/url-shortener.git
cd url-shortener
```

2. Suba os containers:
```bash
docker-compose up -d
```

3. Acesse o Swagger:
http://localhost:8080/swagger-ui/index.html

## 🔐 Autenticação

A API utiliza JWT. Para acessar endpoints protegidos:

1. Cadastre um usuário em `POST /auth/register`
2. Faça login em `POST /auth/login`
3. Copie o token retornado
4. Clique em **Authorize** no Swagger e cole o token

## 📋 Endpoints

### Auth
| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| POST | /auth/register | Cadastra usuário | Público |
| POST | /auth/login | Realiza login | Público |

### URLs
| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| POST | /urls | Cria URL encurtada | Autenticado |
| GET | /urls | Lista URLs do usuário | Autenticado |
| DELETE | /urls/{shortCode} | Remove URL | Autenticado |

### Redirect
| Método | Endpoint | Descrição | Acesso |
|--------|----------|-----------|--------|
| GET | /{shortCode} | Redireciona para URL original | Público |

## 🗄️ Arquitetura
src/main/java/com/enzo/url/shortener/
├── config/         # Configurações (Security, Redis, Swagger)
├── controller/     # Controllers REST
├── domain/         # Entidades JPA
├── dto/            # Records de request/response
├── exception/      # Exceções customizadas e handler global
├── repository/     # Repositórios JPA e cache Redis
├── security/       # JWT Filter, Service e UserDetails
├── service/        # Regras de negócio
└── util/           # Utilitários (gerador de código)

## 🧪 Testes
```bash
./mvnw test
```

## 👤 Autor

**Enzo Anthony**  
Backend Developer | Java | Spring Boot | AWS  
[LinkedIn](https://www.linkedin.com/in/enzo-anthony/) | [GitHub](https://github.com/EnzoAnthony006)