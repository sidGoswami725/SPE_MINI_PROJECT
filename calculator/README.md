# Calculator REST API — SPE Mini Project

A Spring Boot REST API for arithmetic operations, supported by an end-to-end CI/CD pipeline using Jenkins, Docker, Docker Compose, and an Nginx reverse proxy.

## Tech Stack

| Layer | Technology |
|---|---|
| Application | Java 21, Spring Boot 3.4.5, Maven |
| Testing | JUnit 5, MockMvc |
| CI/CD | Jenkins Declarative Pipeline |
| Containerization | Docker, multi-stage Dockerfile |
| Deployment | Docker Compose, Nginx reverse proxy |
| Source control and automation | Git, GitHub webhooks |
| Notifications | Jenkins Email Extension Plugin, SMTP |

## Features

- REST endpoints for addition, subtraction, multiplication, and division.
- Structured JSON responses containing the operation, operands, and result.
- Health endpoint for deployment verification.
- Automated build, test, container image build and push, and deployment through Jenkins.
- GitHub push webhook to trigger Jenkins builds.
- Email notifications for successful and failed pipeline runs.

## REST API

When the application runs directly on the default Spring Boot port, use `http://localhost:8080`. When deployed through the Docker Compose stack with Nginx, use `http://localhost`.

All arithmetic endpoints accept numeric query parameters `a` and `b`.

| Operation | Method and endpoint | Example result |
|---|---|---|
| Add | `GET /api/v1/add?a=5&b=3` | `8.0` |
| Subtract | `GET /api/v1/subtract?a=5&b=3` | `2.0` |
| Multiply | `GET /api/v1/multiply?a=5&b=3` | `15.0` |
| Divide | `GET /api/v1/divide?a=6&b=3` | `2.0` |
| Health | `GET /actuator/health` | `{"status":"UP"}` when healthy |

### Example request

```http
GET http://localhost/api/v1/add?a=5&b=3
```

Example JSON response:

```json
{
  "operation": "add",
  "a": 5.0,
  "b": 3.0,
  "result": 8.0
}
```

The response fields are:
- `operation`: name of the arithmetic operation.
- `a` and `b`: input operands.
- `result`: calculated result.

### Error handling

Invalid operations, including division by zero, are handled by the application's exception handling. A division-by-zero response is intended to use HTTP `400 Bad Request`, with an error message such as `Division by zero is not allowed`.

## Project Structure

```text
calculator/
├── src/main/java/com/spe/calculator/
│   ├── CalculatorApplication.java
│   ├── controller/CalculatorController.java
│   ├── service/CalculatorService.java
│   ├── dto/CalculatorResponse.java
│   └── exception/ApiExceptionHandler.java
├── src/test/java/com/spe/calculator/
│   ├── CalculatorServiceTest.java
│   ├── CalculatorIntegrationTest.java
│   └── CalculatorApplicationTests.java
├── nginx/nginx.conf
├── Dockerfile
├── .dockerignore
├── docker-compose.yml
├── Jenkinsfile
└── pom.xml
```

## Prerequisites

- Java 21
- Git
- Docker Desktop with Docker Compose
- A Docker Hub account for publishing images
- Jenkins configured with GitHub and Docker Hub credentials for CI/CD

The Maven Wrapper (`mvnw` / `mvnw.cmd`) is included, so a separate Maven installation is not required.

## Build and Run Locally

Run commands from the directory containing `pom.xml`.

### 1. Run tests

**Windows PowerShell:**

```powershell
.\mvnw.cmd test
```

**Linux/macOS:**

```bash
./mvnw test
```

### 2. Package the application

**Windows PowerShell:**

```powershell
.\mvnw.cmd clean package
```

**Linux/macOS:**

```bash
./mvnw clean package
```

### 3. Run the packaged JAR

```powershell
java -jar target/calculator-0.0.1-SNAPSHOT.jar
```

The API should be available at `http://localhost:8080`.

### 4. Build and run a Docker image

```powershell
docker build -t calculator:local .
docker run --rm -p 8080:8080 calculator:local
```

The API should be available at `http://localhost:8080`.

### 5. Run with Docker Compose and Nginx

The Compose configuration uses the `DOCKER_HUB_USER` and `BUILD_NUMBER` variables with defaults. To use your own Docker Hub username in PowerShell:

```powershell
$env:DOCKER_HUB_USER = "your-dockerhub-username"
$env:BUILD_NUMBER = "latest"
docker compose up --build -d
docker compose ps
```

To use the defaults in PowerShell, simply run:

```powershell
docker compose up --build -d
docker compose ps
```

For Linux/macOS shells, you can set the username like this:

```bash
export DOCKER_HUB_USER=your-dockerhub-username
docker compose up --build -d
```

When the stack is running, use `http://localhost/api/v1/...` for API requests through Nginx. The health endpoint is `http://localhost/actuator/health`.

## Useful Verification Commands

Check the API health endpoint:

```powershell
Invoke-RestMethod http://localhost/actuator/health
```

Try an arithmetic endpoint:

```powershell
Invoke-RestMethod "http://localhost/api/v1/add?a=5&b=3"
```

List Compose services and their status:

```powershell
docker compose ps
```

View application logs:

```powershell
docker logs calculator-api
```

View the Nginx proxy logs:

```powershell
docker logs calculator-proxy
```

## CI/CD Pipeline

The Jenkins Declarative Pipeline automates these stages:

| Stage | Action |
|---|---|
| Checkout | Checks out source code from GitHub |
| Build | Packages the application |
| Test | Runs automated tests and publishes JUnit results |
| Docker Build | Builds and tags the application image |
| Push | Pushes the image to Docker Hub using Jenkins credentials |
| Deploy | Pulls the image, starts the Compose stack, and checks application health |

The pipeline uses Jenkins-managed credentials rather than storing Docker Hub secrets in the repository.

### GitHub push trigger

The Jenkins job can be configured with **GitHub hook trigger for GITScm polling**. The GitHub repository webhook should point to the Jenkins GitHub webhook endpoint:

```text
https://<public-tunnel-host>/github-webhook/
```

For a local Jenkins instance exposed using ngrok, the ngrok tunnel must be running and forwarding to the Jenkins port for GitHub to reach the endpoint. A temporary ngrok URL can change between sessions; if it changes, update the webhook URL in GitHub.

### Email notifications

The pipeline can send success and failure notifications using the Jenkins Email Extension Plugin (`emailext`). SMTP settings belong in Jenkins system configuration. For Gmail, use an App Password where required; do not commit email passwords, app passwords, or other secrets to this repository.

## Troubleshooting

- **GitHub webhook delivery fails:** Confirm Jenkins is running, the ngrok tunnel forwards to the correct Jenkins port, and the webhook URL ends in `/github-webhook/`. Inspect GitHub's Recent Deliveries for the response code.
- **Build does not trigger:** Confirm the Jenkins job's GitHub hook trigger is enabled and that the push targets the branch configured by the job.
- **Email is not received:** Check Jenkins SMTP settings, authentication, TLS/port configuration, recipient address, spam folder, and the build console output.
- **API health check fails:** Run `docker compose ps` and inspect logs with `docker logs calculator-api` and `docker logs calculator-proxy`.
- **Docker Hub push fails:** Verify the Jenkins credential ID and that the credential has permission to push to the configured repository.

## Docker Hub

The published image repository is at:

https://hub.docker.com/r/sidgoswami725/calculator-api

## Security Notes

- Keep Docker Hub credentials in Jenkins Credentials.
- Keep SMTP passwords and Gmail App Passwords out of Git.
- If credentials have ever been committed to a repository, rotate them and remove them from the repository history as appropriate.
