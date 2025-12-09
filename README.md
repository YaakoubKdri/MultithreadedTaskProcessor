# Multithreaded Task Processor

A small Spring Boot project demonstrating real Java concurrency:
- Thread pools (`ExecutorService`)
- `Callable` + `Future`
- Synchronization (`ReentrantLock`, `synchronized`)
- Task tracking and metadata
- REST API for submitting, monitoring, and cancelling tasks
- Dashboard endpoint to view active and completed tasks
---

## Features

### Core Concurrency
- Fixed thread pool with configurable size
- `Callable<TaskResult>` for task execution
- `Future` for result tracking
- Interrupt-aware cancellation (`future.cancel(true)`)
- Execution time measurement using `System.nanoTime()`
- Thread-safe task registry using `ConcurrentHashMap`
- `ReentrantLock` for controlled synchronization of shared counters

### REST API
- **POST `/api/v1/tasks/submit`** — Submit a new task
- **GET `/api/v1/tasks/{id}`** — Get task status
- **GET `/api/v1/tasks`** — Dashboard (view all tasks)
- **DELETE `/api/v1/tasks/{id}`** — Cancel a running task

### Graceful Shutdown
- Executor service shuts down cleanly using `@PreDestroy`

---

## Tech Stack

- Java 17+
- Spring Boot 3.x
- Maven
- Java Concurrency API (`java.util.concurrent`)
- Lombok
- Postman for API testing

---

## ▶️ Build and Run

### Using Maven
```sh
mvn spring-boot:run
 ```
Application starts at:

`http://localhost:8080`

---

## 🧪 Testing with Postman

### 1. Submit a task
`POST http://localhost:8080/api/v1/tasks/submit?type=SEND_EMAIL`

### 2. Check a task status
`GET http://localhost:8080/api/v1/tasks/{id}`

### 3. Dashboard (all tasks)
`GET http://localhost:8080/api/v1/tasks`

### 4. Cancel a task
`DELETE http://localhost:8080/api/v1/tasks/{id}`


