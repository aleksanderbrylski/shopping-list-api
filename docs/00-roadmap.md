# Shopping List App — Spring Learning Roadmap

## What You're Building

A multi-user REST API where:
- Users belong to a **Household** (you and your wife share one)
- Each household has its own **Storage** (ingredients on hand)
- Each household has its own **Recipes**
- The API accepts a list of recipe IDs → returns a **Shopping List** (what to buy after subtracting storage)
- Authentication via **JWT tokens** (Spring Security)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Database access | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Security | Spring Security + JWT |
| Build tool | Maven |
| API style | REST (JSON) |

---

## Learning Phases

Each phase builds directly on the previous one. Don't skip ahead.

---

### Phase 1 — Java & Maven Basics
**Goal:** Understand the language and build tool before touching Spring.

Topics:
- Java syntax: classes, interfaces, generics, enums
- Records (Java 16+) — you'll use these for API request/response objects
- Maven: what `pom.xml` is, what dependencies are, how to run `mvn` commands
- What a JAR file is

Deliverable: A plain Java Maven project that compiles and runs a `main()` method.

---

### Phase 2 — Spring Core Concepts
**Goal:** Understand *why* Spring exists before using Spring Boot.

Topics:
- What is the Spring IoC container?
- What is a Bean?
- What is Dependency Injection, and why is `@Autowired` / constructor injection used?
- `@Component`, `@Service`, `@Repository` — what each annotation means

Deliverable: A small Spring (not Boot) app with two classes wired together via DI.

---

### Phase 3 — Spring Boot & First REST Endpoints
**Goal:** Build a running API with hardcoded data.

Topics:
- What Spring Boot auto-configuration does
- `@SpringBootApplication` entry point
- `@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable`
- Returning proper HTTP status codes with `ResponseEntity`
- What a DTO (Data Transfer Object) is — use Java Records

Deliverable: A `/recipes` endpoint that returns a hardcoded list of recipes as JSON.

---

### Phase 4 — Spring Data JPA & PostgreSQL
**Goal:** Replace hardcoded data with a real database.

Topics:
- What JPA and Hibernate are (ORM concept)
- `@Entity`, `@Id`, `@GeneratedValue`, `@Column`
- Relationships: `@ManyToOne`, `@OneToMany`, `@ManyToMany`
- `JpaRepository` — what it gives you for free
- `application.properties` — configuring the DB connection
- Database migrations with **Flyway** (you write SQL, Flyway runs it)

Data model to implement:
```
User ──── ManyToOne ──── Household
Household ──── OneToMany ──── Recipe
Household ──── OneToMany ──── StorageItem
Recipe ──── OneToMany ──── RecipeIngredient
StorageItem { ingredient_name, quantity_grams }
RecipeIngredient { ingredient_name, quantity_grams }
```

Deliverable: Full CRUD endpoints for Recipes and Storage backed by PostgreSQL.

---

### Phase 5 — The Shopping List Algorithm
**Goal:** Implement the core business logic.

Topics:
- Service layer pattern — keep logic out of controllers
- Working with Java streams and collections
- Unit conversion logic (grams in DB → kg in response when ≥ 1000g)

Algorithm (you implement this in a `ShoppingListService`):
1. Accept a list of Recipe IDs
2. Load all recipes + their ingredients
3. Sum up total grams needed per ingredient (same recipe can appear multiple times)
4. Load current storage for the household
5. For each needed ingredient: `needed - inStorage` (floor at 0)
6. Return only items where the result > 0

Endpoint: `POST /households/{id}/shopping-list`
Request body: `{ "recipeIds": [1, 2, 2, 3] }`
Response: list of `{ ingredientName, quantityGrams, quantityDisplay }` (display uses kg if ≥ 1000g)

Deliverable: Working shopping list generation endpoint.

---

### Phase 6 — Spring Security & JWT
**Goal:** Lock down the API so users only see their own household's data.

Topics:
- What Spring Security's filter chain is
- `UserDetailsService` — how Spring loads a user for authentication
- Password hashing with `BCryptPasswordEncoder`
- What a JWT is (header.payload.signature)
- Implementing a `JwtFilter` that validates tokens on every request
- `@PreAuthorize` or manual checks — ensuring a user can only access their own household

Endpoints to add:
- `POST /auth/register` — create user + household or join existing one
- `POST /auth/login` — returns a JWT token

Deliverable: All existing endpoints require a valid JWT. A user can only read/write their own household's recipes and storage.

---

### Phase 7 — Polish & Deployment Prep
**Goal:** Make the API production-ready.

Topics:
- Global exception handling with `@ControllerAdvice`
- Input validation with `@Valid` and Bean Validation (`@NotNull`, `@Min`, etc.)
- Proper error response format `{ "error": "...", "status": 400 }`
- Environment-specific config (dev vs prod profiles)
- Dockerizing the app (`Dockerfile` + `docker-compose.yml` with Postgres)

Deliverable: App runs in Docker, ready to push to any hosting provider.

---

## Key Design Decisions (so you understand the "why")

**Why grams in DB, kg in response?**
Storing everything in one unit avoids conversion bugs. The display layer (API response) handles human-readable formatting. This is a common pattern called "canonical unit storage."

**Why Flyway instead of `spring.jpa.hibernate.ddl-auto=create`?**
`ddl-auto=create` destroys your data every restart. Flyway gives you versioned SQL migrations — same as a real production app.

**Why JWTs instead of sessions?**
Sessions require server-side state, which complicates scaling and works poorly with a separate frontend. JWTs are stateless — the frontend just sends a token with each request. Standard for modern REST APIs.

**Why a Household entity instead of just Users?**
It's the cleanest way to model shared access. Instead of complex user-to-user relationships, you have a single shared resource (Household) that multiple users belong to.

---

## GitHub Setup

Do this once, before Phase 1.

**1. Create the repository**
- Go to [github.com](https://github.com) → click **"New"** (top left, green button)
- Repository name: `shopping-list-api`
- Set to **Private** (you can make it public later)
- Check **"Add a README file"**
- Choose **".gitignore template: Java"** — this prevents build output and IDE files from being committed
- Click **"Create repository"**

**2. Install Git locally**
- Download from [git-scm.com](https://git-scm.com) if you don't have it
- Verify: open a terminal and run `git --version`

**3. Configure Git with your identity** (one-time setup)
```bash
git config --global user.name "Your Name"
git config --global user.email "your@email.com"
```

**4. Clone the repo to your machine**
- On your new GitHub repo page, click the green **"Code"** button → copy the HTTPS URL
- In your terminal, navigate to where you want the project, then:
```bash
git clone https://github.com/YOUR_USERNAME/shopping-list-api.git
cd shopping-list-api
```

**5. The basic Git workflow you'll use throughout the project**
```bash
git status                        # see what changed
git add .                         # stage all changes
git commit -m "what you did"      # save a snapshot
git push                          # upload to GitHub
```

**Recommended commit habit:** commit at the end of every working session, or whenever something new works. Small, frequent commits are better than one giant commit at the end.

**Tip — commit messages for this project:** use a simple format like:
- `phase-1: maven project setup`
- `phase-3: add GET /recipes endpoint`
- `phase-4: add Recipe and Ingredient entities`

This way your GitHub history doubles as a learning diary.

---

## How We'll Work

1. You tell me you're ready to start a phase
2. I give you step-by-step instructions for that phase
3. You build it, ask me questions when stuck
4. Once it works, we move to the next phase

Start with: **"I'm ready for Phase 1"**
