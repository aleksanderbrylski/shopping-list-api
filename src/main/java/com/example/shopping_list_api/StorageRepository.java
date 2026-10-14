package com.example.shopping_list_api;

import java.util.List;

public interface StorageRepository {
    List<Ingredient> findAll();
}
