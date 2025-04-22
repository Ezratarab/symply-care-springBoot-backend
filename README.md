# 📆 Symply-Care – Backend (Spring Boot)

Symply-Care is a comprehensive healthcare platform designed to streamline patient management, appointment scheduling, and predictive analytics through seamless integration with microservices.
This backend service, built with Spring Boot, forms the core of the system by exposing RESTful API endpoints, handling business logic, persisting data in relational databases, and enabling asynchronous communication with other components via RabbitMQ.  
It also implements **JWT (JSON Web Token)** authentication for secure access and uses **DTO (Data Transfer Object)** patterns to safely structure and validate data between layers.

![logo](./symply_care_new.png)

<table align="center">
  <tr>
    <td align="center" width="33%">
      <a href="https://github.com/Ezratarab/symply-care-springBoot-backend">
        <img src="https://img.shields.io/badge/Backend-Spring_Boot-6DB33F?style=for-the-badge&logo=spring" alt="Backend">
      </a>
      <br>
      <sub><b>API & Data Management</b></sub>
      <br>
      <sub>Java • Spring Boot • MySQL • RabbitMQ</sub>
    </td>
    <td align="center" width="33%">
      <a href="https://github.com/Ezratarab/symply-care-react-frontend">
        <img src="https://img.shields.io/badge/Frontend-React-61DAFB?style=for-the-badge&logo=react" alt="Frontend">
      </a>
      <br>
      <sub><b>User Interface</b></sub>
      <br>
      <sub>React • Next.js • TypeScript • Tailwind</sub>
    </td>
    <td align="center" width="33%">
      <a href="https://github.com/Ezratarab/symply-care-ML">
        <img src="https://img.shields.io/badge/ML_Service-Flask-000000?style=for-the-badge&logo=flask" alt="ML Service">
      </a>
      <br>
      <sub><b>Predictive Engine</b></sub>
      <br>
      <sub>Python • Scikit-learn • TensorFlow • RabbitMQ</sub>
    </td>
  </tr>
</table>

---

## 🚀 Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- JWT
- RabbitMQ
- MySQL / PostgreSQL
- Docker
- Kubernetes (K8s)

---

## ⚙️ Getting Started

1. **Clone the repository:**

```bash
git clone https://github.com/Ezratarab/symply-care-springBoot-backend.git
cd symply-care-springBoot-backend
```

2. **Build the project:**

```bash
./mvnw clean install
```

3. **Run the application:**

```bash
./mvnw spring-boot:run
```

The app will start on `http://localhost:8080`.

---

## 🐇 RabbitMQ Messaging

RabbitMQ is used for messaging between services. Make sure RabbitMQ is running locally or via Docker/Kubernetes.

- Example queue: `ml.predict.queue`
- Exchange type: `direct` / `topic`
- Spring Boot auto-configures the connection if `spring.rabbitmq.*` properties are set

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

## 🐳 Docker Deployment

1. **Build Docker image:**

```bash
docker build -t symply-care-backend .
```

2. **Run the container:**

```bash
docker run -p 8080:8080 symply-care-backend
```

---

## ☘️ Kubernetes Deployment

1. **Deploy to cluster:**

```bash
kubectl apply -f kubernetes/deployment-backend.yaml
kubectl apply -f kubernetes/service-backend.yaml
```

Make sure your K8s cluster is running and configured with access to RabbitMQ and MySQL.

---

## 🛠️ Environment Variables

Configure database and RabbitMQ in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/symplycare
spring.datasource.username=root
spring.datasource.password=your_password

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

spring.jpa.hibernate.ddl-auto=update
```

---

## 🧪 Run Tests

```bash
./mvnw test
```

---

## 🧠 About

This backend is part of a full-stack system that includes:
- **Frontend** (React) repo named: symply-care-react-frontend
- **Machine Learning Microservice** (Flask + Python) repo named: symply-care-ML
- **Message Queue** (RabbitMQ) is in this repo

It integrates with these services via REST and messaging to deliver a robust, scalable healthcare platform.

---

