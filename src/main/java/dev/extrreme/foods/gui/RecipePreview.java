package dev.extrreme.foods.gui;

import dev.extrreme.foods.FoodsPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.ArrayList;

public class RecipePreview {

    private final Inventory recipePreview;

    public RecipePreview(Recipe recipe, FoodsPlugin foodsPlugin) {
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe craftingRecipe = (ShapedRecipe) recipe;
            recipePreview = foodsPlugin.getServer().createInventory(null, InventoryType.WORKBENCH, "Food Preview");

            for (int i = 0; i/3 < craftingRecipe.getShape().length; i += 3) {
                ArrayList<Character> keys = new ArrayList<>();

                for (char key : craftingRecipe.getShape()[i/3].toCharArray()) {
                    keys.add(key);
                }
                for (char key : craftingRecipe.getShape()[i/3].toCharArray()) {
                    recipePreview.setItem(i + 1 + keys.indexOf(key), craftingRecipe.getIngredientMap().get(key));
                }
            }
            recipePreview.setItem(0, recipe.getResult());
        }
        else if (recipe instanceof FurnaceRecipe) {
            recipePreview = foodsPlugin.getServer().createInventory(null, InventoryType.FURNACE, "Food Preview");
            recipePreview.setItem(0, ((FurnaceRecipe) recipe).getInput());
            recipePreview.setItem(1, new ItemStack(Material.COAL, 1));
            recipePreview.setItem(2, recipe.getResult());
        }
        else {
            recipePreview = null;
        }
    }

    public void open(Player player) {
        if (recipePreview == null) {
            return;
        }
        player.openInventory(recipePreview);
    }
}
