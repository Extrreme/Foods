package dev.extrreme.foods.listener;

import dev.extrreme.foods.EatTimer;
import dev.extrreme.foods.Food;
import dev.extrreme.foods.FoodsPlugin;
import dev.extrreme.foods.util.GeneralUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class FoodListener implements Listener {

    private final FoodsPlugin foodsPlugin;

    public FoodListener(FoodsPlugin foodsPlugin) {
        this.foodsPlugin = foodsPlugin;
    }

    @EventHandler
    public void foodHeadPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = null;

        if (event.getHand() == EquipmentSlot.HAND) {
            handItem = player.getInventory().getItemInMainHand();
        }
        else if (event.getHand() == EquipmentSlot.OFF_HAND) {
            handItem = player.getInventory().getItemInOffHand();
        }
        if (handItem == null) {
            return;
        }

        Food foodInHand = foodsPlugin.getFoodManager().getFoodFromItem(handItem);
        if (foodInHand == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void foodHeadEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) {
            return;
        }

        if (foodsPlugin.getFoodManager().getFoodFromItem(event.getCursor()) == null) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void foodConsume(PlayerInteractEvent event) {
        Food food = foodsPlugin.getFoodManager().getFoodFromItem(event.getPlayer().getInventory().getItemInMainHand());
        if (food == null || foodsPlugin.getFoodManager().isEating(event.getPlayer())) {
            return;
        }

        if (event.getPlayer().getFoodLevel() == 20) {
            event.getPlayer().sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("not-hungry"));
            return;
        }

        event.setCancelled(true);

        foodsPlugin.getFoodManager().getActiveTimers().add(new EatTimer(food, event.getPlayer(), foodsPlugin));

        event.getPlayer().sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("started-eating-food")
                .replace("%food%", food.getItem().getItemMeta().getDisplayName()));
    }

    @EventHandler
    public void eaterMove(PlayerMoveEvent event) {
        if (event.getTo().getY() > event.getFrom().getY() && foodsPlugin.getFoodManager().isEating(event.getPlayer())) {
            event.getTo().setY(event.getFrom().getY());
        }
    }

    @EventHandler
    public void foodTalk(AsyncPlayerChatEvent event) {
        if (!foodsPlugin.getFoodManager().isEating(event.getPlayer())) {
            return;
        }
        event.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC +
                "Its impolite to talk with your mouth full...");
        event.setMessage(GeneralUtils.shuffle(event.getMessage()));
    }

    @EventHandler
    public void furnaceBurn(FurnaceBurnEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        if (event.getBlock() == null || !furnace.getInventory().getTitle().equalsIgnoreCase("Food Preview")) {
            return;
        }
        event.setCancelled(true);
    }
}
