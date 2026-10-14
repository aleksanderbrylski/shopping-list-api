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