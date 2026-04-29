# Implementation Plan — Java Migration Demo

## 1. What the Demo Proves

An AI agent (Devin) can take a realistic Java 8 enterprise monolith — complete with XML-based Spring configuration, legacy date/time APIs, tightly coupled services, and seeded bugs — and migrate it to Java 21 + Spring Boot 3.x microservices architecture, while simultaneously identifying and fixing bugs and implementing enhancements, all in a single live session.

## 2. What Devin Does Live

Devin migrates the Java 8 monolith to Java 21 + Spring Boot 3.x, refactors legacy patterns (XML config → annotation-based, `java.util.Date` → `java.time`, field injection → constructor injection), fixes seeded bugs, and implements enhancement TODOs — then opens a PR with all changes.

## 3. Stack and Rationale

| Dependency | Version | Rationale | Source |
|---|---|---|---|
| Java | 8 (source/target) | Legacy baseline — no records, no var, no modern streams | [Oracle Java 8 docs](https://docs.oracle.com/javase/8/docs/) |
| Spring Boot | 2.7.18 | Last 2.x release supporting Java 8; migration target is 3.x (requires Java 17+) | [Spring Boot 2.7.x EOL](https://spring.io/projects/spring-boot#support) |
| Spring MVC | 5.3.x (via Boot 2.7) | Legacy MVC patterns with XML config alongside annotations | [Spring Framework 5.3 docs](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/) |
| H2 Database | 2.1.x | Embedded DB for zero-config runnability | [H2 Database](https://h2database.com/) |
| Spring Data JPA | 2.7.x | Persistence layer with Hibernate | [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/docs/2.7.x/reference/html/) |
| springdoc-openapi | 1.8.0 | Swagger UI for visual API exploration (works with Boot 2.x) | [springdoc.org](https://springdoc.org/) |
| Thymeleaf | 3.0.x | Server-side rendered dashboard | [Thymeleaf docs](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html) |
| javax.validation | 2.0 | Bean Validation (migrates to jakarta.validation in Boot 3.x) | [JSR 380](https://beanvalidation.org/2.0/spec/) |

## 4. Repo Layout

```
java-migration-demo/
├── docs/
│   ├── IMPLEMENTATION_PLAN.md       ← This file
│   ├── flowchart.html               ← Interactive demo flow diagram (Mermaid)
│   └── flowchart.png                ← Rasterized flowchart for README
├── src/main/java/com/acme/insurance/
│   ├── InsuranceApplication.java    ← Spring Boot main class with @ImportResource
│   ├── config/
│   │   └── AppConfig.java           ← Java config that imports XML — legacy pattern
│   ├── controller/
│   │   ├── PolicyController.java    ← REST endpoints for CRUD on policies
│   │   ├── ClaimController.java     ← REST endpoints for claims processing
│   │   └── DashboardController.java ← Thymeleaf dashboard controller
│   ├── model/
│   │   ├── Policy.java              ← JPA entity — uses java.util.Date
│   │   ├── Claim.java               ← JPA entity — uses java.util.Date
│   │   ├── Customer.java            ← JPA entity
│   │   ├── ClaimStatus.java         ← Enum for claim lifecycle
│   │   └── PolicyStatus.java        ← Enum for policy states
│   ├── repository/
│   │   ├── PolicyRepository.java    ← Spring Data JPA repository
│   │   ├── ClaimRepository.java     ← Spring Data JPA repository
│   │   └── CustomerRepository.java  ← Spring Data JPA repository
│   ├── service/
│   │   ├── PolicyService.java       ← Policy business logic, field injection
│   │   ├── ClaimService.java        ← Claims processing — BUG: NPE on missing customer
│   │   ├── PremiumCalculator.java   ← Premium math — BUG: off-by-one date calculation
│   │   └── PolicyNumberGenerator.java ← Sequence generator — BUG: race condition
│   ├── dto/
│   │   ├── PolicyDTO.java           ← Manual mapping (no MapStruct), verbose getters/setters
│   │   └── ClaimDTO.java            ← Manual mapping, verbose getters/setters
│   └── util/
│       ├── DateUtils.java           ← Legacy java.util.Date/Calendar helpers
│       └── Constants.java           ← Hardcoded config values (FIXME seeded)
├── src/main/resources/
│   ├── application.properties       ← H2 config, server port, Swagger settings
│   ├── applicationContext.xml       ← Legacy XML bean definitions
│   ├── data.sql                     ← Seed data for demo
│   └── templates/
│       └── dashboard.html           ← Thymeleaf dashboard template
├── src/main/webapp/WEB-INF/
│   └── web.xml                      ← Legacy deployment descriptor (informational)
├── pom.xml                          ← Maven build with Spring Boot 2.7.18 parent
├── README.md                        ← Demo documentation
├── DEMO_NOTES.md                    ← Presenter cheat sheet (5-8 bullets)
└── .github/workflows/ci.yml         ← Simple CI: build + test
```

**File count**: ~25 source files (within 10-25 target range).

## 5. Flowchart Outline

**Nodes**:
1. **Trigger**: Presenter prompts Devin in a live session
2. **Analysis**: Devin analyzes the Java 8 monolith structure
3. **Migration — Part 1a**: Upgrade pom.xml (Boot 2.7 → 3.x, Java 8 → 21, javax → jakarta)
4. **Migration — Part 1b**: Refactor XML config → annotation-based Spring Boot auto-config
5. **Migration — Part 1c**: Modernize Java idioms (Date → LocalDate, add records, var, streams)
6. **Migration — Part 1d**: Refactor field injection → constructor injection
7. **Bug Fixes — Part 2a**: Fix NPE in ClaimService
8. **Bug Fixes — Part 2b**: Fix date calculation in PremiumCalculator
9. **Bug Fixes — Part 2c**: Fix race condition in PolicyNumberGenerator
10. **Enhancements**: Implement TODO items (pagination, config externalization, validation)
11. **Build & Test**: Devin runs build and verifies all endpoints
12. **PR**: Devin opens PR with all changes + Swagger UI screenshot
13. **Decision**: Presenter reviews PR, shows diff to audience

**Edges**: Linear flow 1→2→3→…→13 with a parallel fork for Part 1 (migration steps) and Part 2 (bug fixes).

## 6. Runtime Plan

```bash
# Clone and run
git clone https://github.com/tedfoley-cog/java-migration-demo.git
cd java-migration-demo
mvn spring-boot:run

# Endpoints
# Swagger UI:       http://localhost:8080/swagger-ui.html
# Dashboard:        http://localhost:8080/dashboard
# Policies API:     http://localhost:8080/api/policies
# Claims API:       http://localhost:8080/api/claims
# H2 Console:       http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:insurancedb)
```

Visual artifacts visible during demo:
- **Swagger UI** showing all REST endpoints with try-it-out capability
- **Thymeleaf dashboard** showing policy/claim summary statistics
- **H2 Console** for direct DB inspection

## 7. CI Plan

Single GitHub Actions workflow (`.github/workflows/ci.yml`):
- Trigger: push to any branch, PR to main
- Matrix: Java 8 only (initial state)
- Steps: checkout → setup-java 8 → mvn verify
- Under 50 lines of YAML

## 8. Risks and Unknowns

| Risk | Mitigation |
|---|---|
| Spring Boot 2.7.18 + Java 8 may have dependency conflicts in CI | Pin all dependency versions explicitly in pom.xml |
| springdoc-openapi 1.x requires specific Boot 2.7 compatibility | Verified: springdoc 1.8.0 supports Boot 2.7.x per springdoc.org |
| H2 2.x has breaking changes from 1.x (MODE=LEGACY may be needed) | Use `MODE=LEGACY` in JDBC URL for compatibility |
| Race condition bug must be reproducible but not crash the app on startup | Use a non-synchronized counter that only fails under concurrent access |
