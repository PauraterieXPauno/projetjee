# projetjee

le meilleur projet qui n'ait jamais projeté

## Stack

- Java 21
- Spring Boot 4.1.2 (Maven)
- Spring Data JPA / Hibernate ORM 7 (Jakarta Persistence)
- H2 (fichier : `./data/bde`) + données initiales (`data.sql`)
- Thymeleaf + Validation
- PostgreSQL (schéma : `scripts/setup.sql`)

## Structure

```
pom.xml
src/main/java/com/bde/entity/    -> Entités JPA (Compte, Evenement, Inscription, Lieu, Role, Staff, StaffType, Statuts)
src/main/java/fr/paurateriexpauno/projetjee/           -> Application Spring Boot
src/main/java/fr/paurateriexpauno/projetjee/controller/ -> Controllers Thymeleaf
src/main/resources/application.properties -> Configuration BDD / JPA
src/main/resources/data.sql               -> Données initiales (rôles, types de staff)
src/main/resources/templates/             -> Pages Thymeleaf (index, evenements, ...)
src/main/resources/static/                -> CSS / ressources statiques
scripts/setup.sql                 -> Schéma PostgreSQL
files/                            -> Fichiers sources d'origine (déjà intégrés)
```

## Build

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run
```

ou

```bash
java -jar target/projetjee-0.0.1-SNAPSHOT.jar
```

- App : http://localhost:8080
- H2 console : http://localhost:8080/h2-console (base `jdbc:h2:file:./data/bde`, user `sa`)
