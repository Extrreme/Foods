package dev.extrreme.foods.listener;

import dev.extrreme.foods.Food;
import dev.extrreme.foods.FoodsPlugin;
import dev.extrreme.foods.gui.RecipePreview;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class GuiListener implements Listener {

    private final FoodsPlugin foodsPlugin;

    private final ItemStack next;
    private final ItemStack back;

    public GuiListener(FoodsPlugin foodsPlugin) {
        this.foodsPlugin = foodsPlugin;

        ItemStack next = new ItemStack(Material.DOUBLE_PLANT);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.DARK_GREEN + "Next");
        nextMeta.setLore(Collections.singletonList(ChatColor.GREEN + "Click to go to the next page."));
        next.setItemMeta(nextMeta);
        this.next = next;

        ItemStack back = new ItemStack(Material.DOUBLE_PLANT);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.DARK_GREEN + "Back");
        backMeta.setLore(Collections.singletonList(ChatColor.GREEN + "Click to go to the previous page."));
        back.setItemMeta(backMeta);
        this.back = back;
    }

    @EventHandler
    public void recipeClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (event.isCancelled() || inv == null ||
                !player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("Food Preview")) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClickedInventory().getTitle().equalsIgnoreCase("Food Preview") ||
                !event.getWhoClicked().hasPermission("foods.give")) {
            return;
        }
        if (event.getInventory().getType() == InventoryType.WORKBENCH && event.getSlot() == 0) {
            event.getWhoClicked().getInventory().addItem(event.getInventory().getItem(0));
        }
        if (event.getInventory().getType() == InventoryType.FURNACE && event.getSlot() == 2) {
            event.getWhoClicked().getInventory().addItem(event.getInventory().getItem(2));
        }
    }

    @EventHandler
    public void recipeDrag(InventoryDragEvent event) {
        if (!event.getInventory().getTitle().equalsIgnoreCase("Food Preview")){
            return;
        }
        for (int slot : event.getRawSlots()) {
            if (slot > event.getInventory().getSize()) {
                continue;
            }
            event.setCancelled(true);
            break;
        }
    }

    @EventHandler
    public void foodsClick(InventoryClickEvent event){
        if (event.isCancelled() || event.getClickedInventory() == null) {
            return;
        }
        String name = ChatColor.stripColor(event.getWhoClicked().getOpenInventory().getTopInventory().getTitle());
        if (!name.contains("Foods - ")) {
            return;
        }
        event.setCancelled(true);
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().isSimilar(next)) {
            foodsPlugin.getFoodsMenu().open((Player) event.getWhoClicked(), Integer.parseInt(name.split(" - ")[1]));
            return;
        }
        if (event.getCurrentItem().isSimilar(back)) {
            foodsPlugin.getFoodsMenu().open((Player) event.getWhoClicked(), Integer.parseInt(name.split(" - ")[1])-2);
            return;
        }

        ItemStack foodItem = event.getCurrentItem();
        if (foodItem.getType() == Material.STAINED_GLASS_PANE) {
            return;
        }
        Food food = foodsPlugin.getFoodManager().getFoodFromItem(foodItem);
        if (food == null) {
            return;
        }
        new RecipePreview(food.getRecipe(), foodsPlugin).open((Player) event.getWhoClicked());
    }
}
