# Auth Service Template
It's a basic example Spring Boot app what allows to: register a user, exchange the token(sign in) using credentials or Google OAuth2 access token. Please feel free to use this project for your needs.

## Requirements
```
- Java
- Maven
- Postgres
```

## Configuration
Open `application.yml` and set:

```
app:
  token:
    secret:
```
```
spring:
  datasource:
    url:
    username:
    password:
```

```
flyway:
  url:
  schemas:
  user:
  password:
```

## Build
`mvn clean install`

## Run
`mvn spring-boot:run`

## Documentation
Swagger UI: http://localhost:8080/swagger-ui/index.html

