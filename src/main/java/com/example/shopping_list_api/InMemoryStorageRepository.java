package com.example.shopping_list_api;

import java.util.List;

public class InMemoryStorageRepository implements StorageRepository {

    @Override 
    public List<Ingredient> findAll() {
        return List.of(
            new Ingredient("flour", 500),
            new Ingredient("sugar ", 100)
        );
    }
    
}