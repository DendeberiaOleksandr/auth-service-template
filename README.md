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
