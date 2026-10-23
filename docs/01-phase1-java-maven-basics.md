# Phase 1 — Java & Maven Basics

**Goal:** Understand the language and build tool before touching Spring.  
By the end you'll have a working Maven project that compiles and runs.

---

## Step 1 — Install the tools

**Java 25 (already installed ✓)**
You already have Java 25. Verify it's still working:
```bash
java -version
javac -version
```

**Maven**
- Download from: [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) — grab the binary zip
- Extract it somewhere like `C:\tools\maven`
- Add `C:\tools\maven\bin` to the existing **Path** system environment variable (not a new variable)
- Open a new terminal and verify:
```bash
mvn -version
```

**IDE — Kiro**
You're already using Kiro. Install these two extensions:
1. Open the Extensions panel (`Ctrl+Shift+X`)
2. Search for **"Extension Pack for Java"** (by Microsoft) → Install
3. Search for **"Spring Boot Extension Pack"** (by VMware) → Install

These give you Java language support, Maven integration, and Spring-specific tooling directly inside Kiro.

---

## Step 2 — Understand the key Java concepts

Before writing any code, read about these. Don't memorize — just get familiar. You'll reinforce them by building.

**Classes and Objects**
Java is object-oriented. Everything lives inside a class.
```java
public class Ingredient {
    private String name;
    private int quantityGrams;

    public Ingredient(String name, int quantityGrams) {
        this.name = name;
        this.quantityGrams = quantityGrams;
    }

    public String getName() {
        return name;
    }
}
```
- `private` means only this class can access the field directly
- The constructor (`public Ingredient(...)`) is what runs when you do `new Ingredient(...)`
- Getters expose the data

**Interfaces**
A contract — defines *what* a class can do, not *how*.
```java
public interface StorageRepository {
    List<Ingredient> findAll();
    void save(Ingredient ingredient);
}
```
Spring uses interfaces heavily. You'll define an interface, and Spring provides the implementation.

**Generics**
The `<T>` syntax means "this works with any type."
```java
List<String> names = new ArrayList<>();
List<Ingredient> ingredients = new ArrayList<>();
```
`List` is generic — it can hold any type. You'll see this constantly.

**Enums**
A fixed set of constants.
```java
public enum Unit {
    GRAMS, KILOGRAMS, MILLILITERS, LITERS
}
```

**Records (Java 16+)**
A shorthand for simple data-carrying classes. No boilerplate.
```java
public record IngredientDto(String name, int quantityGrams) {}
```
This automatically gives you a constructor, getters, `equals()`, `hashCode()`, and `toString()`. You'll use records as API request/response objects throughout the project.

**Streams and Collections**
A modern way to work with lists. You'll use this in the shopping list algorithm.
```java
List<Integer> quantities = List.of(100, 200, 300);

int total = quantities.stream()
    .reduce(0, Integer::sum);  // 600

List<Integer> big = quantities.stream()
    .filter(q -> q > 100)
    .toList();  // [200, 300]
```

---

## Step 3 — Create your Maven project

Use **Spring Initializr** to generate the project — it's the standard way to start any Spring Boot project.

1. Go to [start.spring.io](https://start.spring.io)
2. Set these options:
   - **Project:** Maven
   - **Language:** Java
   - **Spring Boot:** leave the default version selected
   - **Group:** `com.example`
   - **Artifact:** `shopping-list-api`
   - **Packaging:** Jar
   - **Java:** 21 (we'll update to 25 in pom.xml after)
   - **Dependencies:** none for now
3. Click **Generate** — it downloads a zip
4. Extract the zip contents into your `C:\Users\olo_b\Desktop\java-spring-project` folder
5. Open that folder in Kiro: **File → Open Folder**

Spring Initializr generates this structure:
```
shopping-list-api/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/shopping_list_api/
    │   │       └── ShoppingListApiApplication.java
    │   └── resources/
    │       └── application.yaml
    └── test/
        └── java/
```

Note: the package folder is `shopping_list_api` (underscores), not `shoppinglist`. Use this package name in all your files.

---

## Step 4 — Understand pom.xml

Open `pom.xml` in Kiro. It's the heart of a Maven project.

After opening, find the `<properties>` section and update the Java version to 25:
```xml
<properties>
    <java.version>25</java.version>
</properties>
```

Key things in the pom.xml Spring Initializr generated:
- `<parent>` — inherits configuration from `spring-boot-starter-parent`, which manages all Spring dependency versions for you
- `<dependencies>` — libraries your project uses; you'll add more here in later phases
- `<build>` — includes the Spring Boot Maven plugin that lets you run the app

Key Maven commands you'll use from the Kiro terminal:
```bash
mvn compile        # compiles your Java files
mvn package        # compiles + creates a JAR file in /target
mvn clean          # deletes the /target folder (build output)
mvn clean package  # fresh build
```

---

## Step 5 — Write your first Java code

In `src/main/java/com/example/shopping_list_api/`, create one new file using Kiro (right-click the folder → New File):

**`Ingredient.java`**
```java
package com.example.shopping_list_api;

public class Ingredient {
    private final String name;
    private final int quantityGrams;

    public Ingredient(String name, int quantityGrams) {
        this.name = name;
        this.quantityGrams = quantityGrams;
    }

    public String getName() {
        return name;
    }

    public int getQuantityGrams() {
        return quantityGrams;
    }

    @Override
    public String toString() {
        return name + ": " + quantityGrams + "g";
    }
}
```

Then **update `ShoppingListApiApplication.java`** (already generated by Spring Initializr — do not create a new file). Replace its contents with:
```java
package com.example.shopping_list_api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

@SpringBootApplication
public class ShoppingListApiApplication {

    public static void main(String[] args) {
        // Plain Java exercise — Spring will be wired up in Phase 3
        List<Ingredient> ingredients = List.of(
            new Ingredient("flour", 500),
            new Ingredient("sugar", 200),
            new Ingredient("butter", 250)
        );

        // Print each ingredient
        ingredients.forEach(i -> System.out.println(i));

        // Calculate total grams using streams
        int total = ingredients.stream()
            .mapToInt(Ingredient::getQuantityGrams)
            .sum();

        System.out.println("Total: " + total + "g");
    }
}
```

Run it from the Kiro terminal:
```bash
mvn spring-boot:run
```

Or click the **Run** button that appears above the `main()` method (the Java extension adds this automatically).

Expected output:
```
flour: 500g
sugar: 200g
butter: 250g
Total: 950g
```

---

## Step 6 — Add a Record and an Interface

**`IngredientDto.java`** (a Record — notice how short it is vs the class above)
```java
package com.example.shopping_list_api;

public record IngredientDto(String name, int quantityGrams) {}
```

**`StorageRepository.java`** (an Interface)
```java
package com.example.shopping_list_api;

import java.util.List;

public interface StorageRepository {
    List<Ingredient> findAll();
}
```

**`InMemoryStorageRepository.java`** (an Implementation of the interface)
```java
package com.example.shopping_list_api;

import java.util.List;

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

Update the `main` method in `ShoppingListApiApplication.java` to use these:
```java
package com.example.shopping_list_api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

@SpringBootApplication
public class ShoppingListApiApplication {

    public static void main(String[] args) {
        StorageRepository repo = new InMemoryStorageRepository();
        List<Ingredient> storage = repo.findAll();
        storage.forEach(System.out::println);
    }
}
```

This pattern — coding to an interface, swapping the implementation — is exactly what Spring does automatically. In Phase 2, Spring will create `InMemoryStorageRepository` for you and inject it wherever `StorageRepository` is needed.

---

## Step 7 — Commit to GitHub

```bash
git add .
git commit -m "phase-1: plain java maven project"
git push
```

Check your GitHub repo — you should see the files there.

---

## Phase 1 Checklist

- [ ] Java 25 verified with `java -version`
- [ ] Maven installed and `mvn -version` works
- [ ] Kiro has **Extension Pack for Java** and **Spring Boot Extension Pack** installed
- [ ] Spring Initializr project created and compiles
- [ ] `pom.xml` updated with Java 25
- [ ] You understand what `pom.xml` is for
- [ ] `ShoppingListApiApplication.java` runs and prints output
- [ ] You understand the difference between a class, interface, and record
- [ ] You've run a basic stream operation
- [ ] Code pushed to GitHub

---

When all boxes are checked, say **"I'm ready for Phase 2"**.
