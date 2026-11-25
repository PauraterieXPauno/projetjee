# projetjee

le meilleur projet qui n'ait jamais projeté

## Stack

- Java 21
- Spring Boot 3.4.5 (Maven)
- Spring Data JPA / Hibernate ORM 6 (Jakarta Persistence)
- H2 (fichier : `./data/bdedb`) + données initiales (`DataInitializer`)
- Thymeleaf + Validation
- API REST (contrôleurs `/api/**`) + back-office MVC (Thymeleaf)

## Structure

```
pom.xml
src/main/java/fr/cytech/bde/                     -> Application Spring Boot, config, domain, repositories, services
src/main/java/fr/cytech/bde/web/api/            -> Contrôleurs REST
src/main/java/fr/cytech/bde/web/mvc/           -> Contrôleurs MVC (Thymeleaf)
src/main/resources/application.properties     -> Configuration BDD / JPA
src/main/resources/static/css/                  -> CSS
src/main/resources/templates/                   -> Pages Thymeleaf (home, evenements, ...)
bruno/                                          -> Tests API (collection Bruno)
```

## Build

```bash
./mvnw clean package
```

## Run

```bash
./mvnw spring-boot:run
```

ou

```bash
java -jar target/bde-manager-1.0.0.jar
```

- App : http://localhost:8080
- H2 console : http://localhost:8080/h2-console (base `jdbc:h2:file:./data/bdedb`, user `sa`)
