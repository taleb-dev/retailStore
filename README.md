# 🏪 Retail Discount System

This is a Java Spring Boot application that calculates **net payable amount** for a retail store bill
based on the following discount rules:

- Employee → 30% discount
- Affiliate → 10% discount
- Loyal Customer (>2 years) → 5% discount
- $5 discount for every $100 on the bill
- Percentage-based discounts do **not** apply on groceries
- Only one percentage-based discount can be applied

---

## 🚀 Technologies
- Java 17
- Spring Boot 3.x
- MongoDB
- Docker + Docker Compose
- JUnit 5 + Mockito
- Jacoco (coverage)

---

## 🏗️ How to Run

### 1. Run with Docker
```bash
docker-compose up --build
