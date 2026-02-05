# 📦 Product API

[![Java](https://img.shields.io/badge/Java-21+-red.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-Build-blue.svg)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

API REST desenvolvida em **Spring Boot** para gerenciamento de produtos.  
O projeto aplica boas práticas de **arquitetura em camadas**, **validação de regras de negócio**, **tratamento de exceções** e **testes unitários**, servindo como base de estudo ou template para APIs REST em Java.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3+**
- **Spring Web**
- **Spring Data JPA**
- **Maven**
- **JUnit 5**
- **Mockito**
- **PostgreSQL**

---

## 🧱 Arquitetura do Projeto

O projeto segue o padrão de **arquitetura em camadas**:

Controller → Service → Repository

### 📁 Estrutura de Pacotes

```
com.hendersonkleber.product
├── controller
├── domain
├── dto
├── exception
├── repository
└── service
```

---

## 🔗 Endpoints Principais

### Produtos

- `GET /products`
- `GET /products/{id}`
- `POST /products`
- `PUT /products/{id}`
- `DELETE /products/{id}`

---

## 📊 Paginação

```
GET /products?page=0&limit=10&sort=id&order=asc
```

---

## 🧪 Testes

Executar testes:

```bash
./mvnw test
```

---

## 👨‍💻 Autor

Desenvolvido por **Henderson Kleber**
