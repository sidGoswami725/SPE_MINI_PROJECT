# Calculator REST API — SPE Mini Project

A Spring Boot REST API for arithmetic operations, wrapped in a full CI/CD pipeline using Jenkins, Docker, and Docker Compose.

## Tech Stack

| Layer | Technology |
|---|---|
| Application | Java 21, Spring Boot 3.4.5, Maven |
| Testing | JUnit 5, MockMvc |
| CI/CD | Jenkins (declarative pipeline) |
| Container | Docker (multi-stage build) |
| Deployment | Docker Compose + Nginx reverse proxy |

---

## REST Endpoints

All endpoints accept query parameters `a` and `b` (numeric).

| Operation | Endpoint | Example |
|---|---|---|
| Add | `GET /api/v1/add?a=&b=` | `/api/v1/add?a=5&b=3` → `{"result":8.0}` |
| Subtract | `GET /api/v1/subtract?a=&b=` | `/api/v1/subtract?a=5&b=3` → `{"result":2.0}` |
| Multiply | `GET /api/v1/multiply?a=&b=` | `/api/v1/multiply?a=5&b=3` → `{"result":15.0}` |
| Divide | `GET /api/v1/divide?a=&b=` | `/api/v1/divide?a=6&b=3` → `{"result":2.0}` |
| Health | `GET /actuator/health` | `{"status":"UP"}` |

**Response format:**
```json
{ "operation": "add", "a": 5.0, "b": 3.0, "result": 8.0 }
```

**Error response (HTTP 400):**
```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "Division by zero is not allowed" }
```

---

## How to Build & Run Locally

### Prerequisites
- Java 21
- Maven (or use the bundled `./mvnw`)
- Docker & Docker Compose

### 1. Run tests
```bash
./mvnw test
```

### 2. Build the JAR
```bash
./mvnw clean package
```

### 3. Run with Java
```bash
java -jar target/calculator-0.0.1-SNAPSHOT.jar
```
API available at `http://localhost:8080`

### 4. Build Docker image
```bash
docker build -t calculator:local .
docker run -p 8080:8080 calculator:local
```

### 5. Run full stack with Docker Compose
```bash
# Set your Docker Hub username (or edit docker-compose.yml)
export DOCKER_HUB_USER=your-dockerhub-username
docker compose up --build
```
API proxied through Nginx at `http://localhost/api/v1/...`

---

## Project Structure

```
calculator/
├── src/main/java/com/spe/calculator/
│   ├── CalculatorApplication.java      # Spring Boot entry point
│   ├── controller/CalculatorController.java
│   ├── service/CalculatorService.java
│   ├── dto/CalculatorResponse.java
│   └── exception/ApiExceptionHandler.java
├── src/test/java/com/spe/calculator/
│   ├── CalculatorServiceTest.java      # Unit tests
│   ├── CalculatorIntegrationTest.java  # MockMvc integration tests
│   └── CalculatorApplicationTests.java # Context load test
├── nginx/nginx.conf                    # Nginx reverse proxy config
├── Dockerfile                          # Multi-stage Docker build
├── .dockerignore
├── docker-compose.yml                  # App + Nginx proxy stack
├── Jenkinsfile                         # Declarative CI/CD pipeline
└── pom.xml
```

---

## CI/CD Pipeline Stages

| Stage | Action |
|---|---|
| Checkout | Pull latest code from GitHub |
| Build | `mvn clean package -DskipTests` |
| Test | `mvn test` + publish JUnit XML results |
| Docker Build | Build & tag image with build number |
| Push | Push to Docker Hub (Jenkins credentials) |
| Deploy | `docker compose up -d` + health check |

Configure the Jenkins job to point to this repository and add a `dockerhub-credentials` entry in the Jenkins Credentials store.
