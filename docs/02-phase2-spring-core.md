# Phase 2 — Spring Core (IoC & Dependency Injection)

**Goal:** Understand how Spring manages objects for you.  
By the end you'll know what a Spring Bean is, how dependency injection works, and why it matters.

---

## The core idea

In Phase 1 you wrote this:
```java
StorageRepository repo = new InMemoryStorageRepository();
```

You created the object yourself with `new`. Spring's entire purpose is to take over this job. You tell Spring *what* you need, and Spring creates and provides it. This is called **Inversion of Control (IoC)** — control over object creation is inverted from you to the framework.

**Dependency Injection (DI)** is how Spring delivers objects. Instead of your class creating its dependencies, Spring *injects* them.

---

## Step 1 — Understand Beans

A **Bean** is just a Java object that Spring creates and manages. You mark a class as a Bean using annotations, and Spring puts it in a container called the **Application Context**.

The most common annotations:

| Annotation | Meaning |
|---|---|
| `@Component` | Generic Spring-managed class |
| `@Service` | Business logic layer (same as @Component, but expresses intent) |
| `@Repository` | Data access layer (same as @Component, adds some DB error handling) |
| `@Controller` / `@RestController` | Web layer — handles HTTP requests |
| `@Configuration` | A class that defines beans manually |

They all register the class as a Bean. The different names communicate the *role* of the class.

---

## Step 2 — Understand @Autowired and Constructor Injection

Once a class is a Bean, Spring can inject it into other Beans.

**The old way (field injection — avoid this):**
```java
@Service
public class StorageService {
    @Autowired  // Spring injects this directly into the field
    private StorageRepository repository;
}
```

**The correct way (constructor injection):**
```java
@Service
public class StorageService {
    private final StorageRepository repository;

    // Spring sees one constructor and injects the dependency automatically
    public StorageService(StorageRepository repository) {
        this.repository = repository;
    }
}
```

Constructor injection is preferred because:
- The field is `final` — it can never be accidentally reassigned
- Dependencies are explicit — you can see exactly what a class needs
- Easier to test — you can pass a mock object directly in tests

With modern Spring (5+), you don't even need `@Autowired` on the constructor if there's only one constructor. Spring injects it automatically.

---

## Step 3 — Rewrite your Phase 1 code using Spring

Now apply this to your project. You'll convert your plain Java classes into Spring Beans.

**Update `InMemoryStorageRepository.java`** — add `@Repository`:
```java
package com.example.shopping_list_api;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class InMemoryStorageRepository implements StorageRepository {

    @Override
    public List<Ingredient> findAll() {
        return List.of(
            new Ingredient("flour", 200),
            new Ingredient("sugar", 100)
        );
    }
}
```

**Create `StorageService.java`** — a new service Bean that uses the repository:
```java
package com.example.shopping_list_api;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StorageService {
    private final StorageRepository repository;

    public StorageService(StorageRepository repository) {
        this.repository = repository;
    }

    public List<Ingredient> getAllIngredients() {
        return repository.findAll();
    }
}
```

Spring will:
1. See `@Repository` on `InMemoryStorageRepository` and create a Bean from it
2. See `@Service` on `StorageService`, look at its constructor, find that it needs a `StorageRepository`
3. Find `InMemoryStorageRepository` in the context (it implements `StorageRepository`)
4. Inject it automatically

You never called `new`. Spring wired everything together.

---

## Step 4 — Use the Application Context

Update `ShoppingListApiApplication.java` to start Spring and use the wired-up service:

```java
package com.example.shopping_list_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ShoppingListApiApplication {

    public static void main(String[] args) {
        // Start Spring — this returns the Application Context (the Bean container)
        ApplicationContext context = SpringApplication.run(ShoppingListApiApplication.class, args);

        // Ask the context for the StorageService Bean
        StorageService service = context.getBean(StorageService.class);

        // Use it — Spring injected the repository for us
        service.getAllIngredients().forEach(System.out::println);
    }
}
```

Run it:
```bash
mvn spring-boot:run
```

You'll see Spring startup logs, then your two ingredients printed at the end. The important thing is that you never called `new StorageService(...)` or `new InMemoryStorageRepository()` — Spring did it.

---

## Step 5 — Understand @SpringBootApplication

Open `ShoppingListApiApplication.java` and look at `@SpringBootApplication`. It's actually three annotations in one:

| Annotation | What it does |
|---|---|
| `@SpringBootConfiguration` | Marks this as a configuration class |
| `@EnableAutoConfiguration` | Tells Spring Boot to auto-configure based on what's on the classpath |
| `@ComponentScan` | Tells Spring to scan this package and all sub-packages for Beans |

`@ComponentScan` is why `@Repository` and `@Service` annotations work — Spring scans the package, finds them, and registers them as Beans automatically. This only works if your classes are in the **same package or a sub-package** as `ShoppingListApiApplication`. Keep that in mind as you add more classes.

---

## Step 6 — Understand the layered architecture

Your project already follows the standard Spring layered pattern. You'll build on this in every phase:

```
HTTP Request
     ↓
[ Controller ]     ← handles HTTP, calls Service
     ↓
[   Service  ]     ← business logic, calls Repository
     ↓
[ Repository ]     ← data access (database in Phase 4)
```

Right now you have:
- `StorageRepository` (interface) + `InMemoryStorageRepository` (implementation) — the repository layer
- `StorageService` — the service layer
- Controller layer — comes in Phase 3

This separation keeps each layer focused on one responsibility. The service doesn't know about HTTP. The repository doesn't know about business logic.

---

## Step 7 — Clean up and commit

Your final file structure for Phase 2:
```
com/example/shopping_list_api/
├── ShoppingListApiApplication.java
├── Ingredient.java
├── IngredientDto.java
├── StorageRepository.java          (interface)
├── InMemoryStorageRepository.java  (@Repository)
└── StorageService.java             (@Service)
```

Commit:
```bash
git add .
git commit -m "phase-2: spring ioc and dependency injection"
git push
```

---

## Phase 2 Checklist

- [ ] You understand what a Bean is
- [ ] You understand the difference between `@Component`, `@Service`, and `@Repository`
- [ ] You understand why constructor injection is preferred over field injection
- [ ] `InMemoryStorageRepository` is annotated with `@Repository`
- [ ] `StorageService` is created with constructor injection
- [ ] App starts with `SpringApplication.run(...)` and prints ingredients
- [ ] You understand what `@SpringBootApplication` does
- [ ] You understand the Controller → Service → Repository layer pattern
- [ ] Code pushed to GitHub

---

When all boxes are checked, say **"I'm ready for Phase 3"**.
