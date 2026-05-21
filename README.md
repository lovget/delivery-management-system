# Delivery Management System

Проект для управления заказами доставки (Spring Boot + PostgreSQL + React/Vite).

## Что добавлено для Lab 8 (Deploy)
- Dockerfile для backend (Java 21, multi-stage build).
- Dockerfile для frontend (Node build + Nginx runtime).
- `docker-compose.yml` для запуска `postgres + backend + frontend` одной командой.
- Перевод backend-конфига на environment variables.
- Healthcheck через Spring Boot Actuator (`/actuator/health`).
- CI/CD workflow в GitHub Actions: build, tests, docker build, health endpoint check.
- `render.yaml` для деплоя на Render Free plan.

---

## Структура deploy-файлов
- `Dockerfile` — контейнеризация Spring Boot backend.
- `frontend/Dockerfile` — контейнеризация frontend.
- `frontend/nginx/default.conf` — SPA fallback конфиг для React-router.
- `docker-compose.yml` — локальный orchestration всех сервисов.
- `.env.example` — пример обязательных переменных окружения.
- `.github/workflows/ci-cd.yml` — CI/CD pipeline.
- `render.yaml` — IaC-конфигурация для Render.

---

## Environment variables
### Backend
- `DB_URL` — JDBC URL PostgreSQL (пример: `jdbc:postgresql://postgres:5432/delivery_db`).
- `DB_USERNAME` — пользователь БД.
- `DB_PASSWORD` — пароль БД.
- `SPRING_JPA_HIBERNATE_DDL_AUTO` — режим миграции схемы (`update` для local/dev).
- `SPRING_JPA_SHOW_SQL` — лог SQL (`true/false`).

### Docker Compose / Postgres
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

### Frontend
- `VITE_API_BASE_URL` — URL backend API для сборки frontend.

---

## Пошаговая инструкция (максимально подробно)

## 1) Что установить
Установить на ПК:
1. **Git**
2. **Docker Desktop** (или Docker Engine + Docker Compose v2)
3. **Java 21** (Temurin/OpenJDK) — для локального запуска без Docker
4. **Node.js 22 LTS** — для локального frontend dev режима

Проверка версий (в терминале):
```bash
git --version
docker --version
docker compose version
java -version
node -v
npm -v
```

## 2) Клонирование проекта
В терминале:
```bash
git clone <YOUR_REPO_URL>
cd delivery-management-system
```

## 3) Подготовка `.env`
В корне проекта:
```bash
cp .env.example .env
```
Открой `.env` и при необходимости измени значения:
```env
POSTGRES_DB=delivery_db
POSTGRES_USER=delivery_user
POSTGRES_PASSWORD=delivery_password
```

## 4) Запуск всех сервисов через Docker Compose
В корне проекта:
```bash
docker compose up -d --build
```

Что поднимется:
- `postgres` (БД)
- `backend` (Spring Boot на `:8080`)
- `frontend` (Nginx + React на `:3000`)

## 5) Проверка что контейнеры работают
```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f postgres
docker compose logs -f frontend
```

Ожидается:
- у `postgres` статус healthy,
- у `backend` статус healthy,
- frontend отвечает на `http://localhost:3000`.

## 6) Как открыть frontend
Открой в браузере:
- `http://localhost:3000`

## 7) Как проверить backend
Открой:
- `http://localhost:8080`

Проверка API (пример):
```bash
curl -i http://localhost:8080/api/orders
```

## 8) Как открыть Swagger
Открой:
- `http://localhost:8080/swagger-ui/index.html`

## 9) Как проверить healthcheck
Открой:
- `http://localhost:8080/actuator/health`

Или в терминале:
```bash
curl -fsS http://localhost:8080/actuator/health
```

Ожидаемый результат — JSON со статусом `UP`.

## 10) Остановка и очистка
Остановить:
```bash
docker compose down
```

Остановить и удалить volume БД:
```bash
docker compose down -v
```

## 11) Локальный запуск без Docker (опционально)
1. Подними PostgreSQL отдельно.
2. Установи env переменные `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
3. Запуск backend:
```bash
./mvnw spring-boot:run
```
4. Запуск frontend:
```bash
cd frontend
npm ci
npm run dev
```

## 12) GitHub Actions CI/CD
Workflow файл: `.github/workflows/ci-cd.yml`

Автоматически выполняются:
1. сборка frontend,
2. `mvn clean verify` backend,
3. сборка Docker image backend,
4. сборка Docker image frontend,
5. запуск `postgres + backend` и проверка `/actuator/health`.

Как проверить:
1. Push в `main/master` или открой PR.
2. Открой вкладку **Actions** в GitHub.
3. Убедись, что job `build-test-docker` зелёный.

## 13) Deploy на Render (бесплатный вариант)
В проекте есть `render.yaml`.

Шаги:
1. Запушь проект в GitHub.
2. Зайди в Render → **New** → **Blueprint**.
3. Подключи GitHub repo.
4. Render прочитает `render.yaml` и создаст:
   - `dms-postgres` (DB),
   - `dms-backend` (web service),
   - `dms-frontend` (web service).
5. Дождись статуса **Live**.
6. Открой публичный URL frontend.

Проверка деплоя:
- frontend URL открывается,
- backend health: `https://<backend-domain>/actuator/health`,
- swagger: `https://<backend-domain>/swagger-ui/index.html`.

## 14) Проверка совместимости frontend/backend после Docker/env migration
Чек-лист:
1. Открываются страницы Customers/Orders/Products/Categories/Payments.
2. Работает CRUD по всем сущностям.
3. Работают фильтрация и пагинация заказов.
4. Нет CORS ошибок в DevTools.
5. Все API-запросы frontend идут в `VITE_API_BASE_URL`.