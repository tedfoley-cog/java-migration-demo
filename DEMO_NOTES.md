# Demo Cheat Sheet — Java Migration & Enhancement

## Setup (do this before joining the call)
- [ ] Open the repo in a browser tab: https://github.com/tedfoley-cog/java-migration-demo
- [ ] Have a fresh Devin session ready (or prepare to start one live)

## Demo Flow
1. Open the repo and walk through the codebase briefly — point out the XML config, `java.util.Date` usage, field injection, and the seeded bugs in `ClaimService`, `PremiumCalculator`, and `PolicyNumberGenerator`
2. Prompt Devin: "Migrate this Java 8 monolith to Java 21 + Spring Boot 3.x. Modernize all legacy patterns, fix the bugs, and implement the TODO/FIXME items. Open a PR with all changes."
3. While Devin works, narrate what it's doing — analyzing the codebase, upgrading the pom.xml, refactoring XML to annotations, modernizing Java idioms, fixing the three seeded bugs
4. Once Devin opens the PR, walk through the diff with the audience — highlight the javax→jakarta migration, Date→LocalDate refactor, constructor injection, and each bug fix
5. Show the Swagger UI and dashboard running on the migrated app to prove it still works
6. "This is what a developer would spend days on — Devin did it in one session, end to end"
