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