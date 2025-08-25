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
- **Docker + Docker Compose**
- **SonarQube** 

---

## 🏗️ How to Run

### 1. Run with Docker
```bash
docker-compose up --build
```
Tip: for insert dummy data for users active **dummy** profile 
- App → http://localhost:8080
- MongoDB → mongodb://localhost:27017/retaildb

### 2. Call the APIs

- Login (get JWT):
```bash
curl -sS -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
        "username": "<your-username>",
        "password": "<your-password>"
      }'
```
Example response:
```json
{"token":"<JWT_TOKEN>"}
```

- Calculate bill (authorized):
```bash
TOKEN="<JWT_TOKEN>"
curl -sS -X POST http://localhost:8080/api/bills/calculate \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
        "items": [
          {"name": "TV", "category": "OTHER", "price": 1000},
          {"name": "Apples", "category": "GROCERY", "price": 50}
        ],
        "totalAmount": 1050
      }'
```
Notes:
- Base path is /api (see application.properties).
- Item.category must be one of: GROCERY, OTHER.
- Only non-grocery items get percentage discounts; $5 per $100 applies to the total amount.

---

## ✅ Run Unit Tests

- Run all tests:
```bash
./mvnw clean test
```

- Run a single test class:
```bash
./mvnw -Dtest=dev.taleb.retail.service.discount.DiscountServiceTest test
```

- Run a single test method:
```bash
./mvnw -Dtest=DiscountServiceTest#shouldApplyEmployeeDiscount test
```

Tip: add -q for quiet logs or -DskipTests to skip running tests during build.

---

## 📊 Code Coverage with JaCoCo

Generate coverage report (HTML):
```bash
./mvnw clean test org.jacoco:jacoco-maven-plugin:prepare-agent org.jacoco:jacoco-maven-plugin:report
```
Then open the report:
```
open target/site/jacoco/index.html   # macOS
xdg-open target/site/jacoco/index.html  # Linux
```

.

---

## 🔎 Static Analysis with SonarQube

You can analyze the project with SonarQube using the Maven scanner without modifying pom.xml.

1) Start SonarQube locally (Docker):
```bash
docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community
```
Wait ~1–2 minutes, then open http://localhost:9000, login (default admin/admin), and create a project token.

2) Run the analysis:
```bash
./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar \
  -Dsonar.projectKey=retail-store \
  -Dsonar.projectName="Retail Store" \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=YOUR_TOKEN_HERE
```

Notes:
- Replace YOUR_TOKEN_HERE with your generated token.
- You can also set SONAR_TOKEN env var and use -Dsonar.token=$SONAR_TOKEN.
- If you have a SonarQube server elsewhere, update -Dsonar.host.url accordingly.

