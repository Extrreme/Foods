package dev.extrreme.foods;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class Food {

    private final String name;
    private final int hunger;
    private final int consumptionTime;
    private final boolean allowMovement;
    private final ItemStack item;
    private final Recipe recipe;

    public Food(String name, int hunger, int consumptionTime, boolean allowMovement, ItemStack item, Recipe recipe) {
        this.name = name;
        this.hunger = hunger;
        this.consumptionTime = consumptionTime;
        this.allowMovement = allowMovement;
        this.item = item;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public int getHunger() {
        return hunger;
    }

    public int getConsumptionTime() {
        return consumptionTime;
    }

    public boolean getMovementPermission() {
        return allowMovement;
    }

    public ItemStack getItem() {
        return item;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
