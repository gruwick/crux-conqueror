#Dissertation

# Crux Conqueror

Crux Conqueror is a bouldering-focused web application designed to help climbers log training sessions, review performance, and track progress over time.

This repository represents an early prototype developed as part of a final-year project, with a focus on core user value and iterative delivery.

---

## Current Features (Prototype v1)

- User registration and authentication
- Secure login using Spring Security
- Login and logout with error messages
- Training session logging
- Viewing previously logged training sessions
- Basic navigation between core pages

---

## Planned Features

- User progress dashboard based on session history
- Training analytics and trend analysis
- Session editing and deletion
- Nutrition tracking (future iteration, Prototype V3)
- Community features and leaderboards (future iteration, Prototype V3)

More advanced features, such as AI-assisted navigation or training recommendations, are considered future work and are intentionally out of scope for the current prototype.

---

## Technology Stack

- Java / Spring Boot
- Thymeleaf
- Spring Security
- JPA / Hibernate
- MySQL (or H2 for development)
- Dbeaver

---

## Running the Project

The application can be run locally using standard Spring Boot tooling:

```bash
mvn spring-boot:run
The aplication will then be accessible via http://localhost:8080
