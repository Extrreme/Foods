package dev.extrreme.foods;

import dev.extrreme.foods.util.ItemUtils;
import dev.extrreme.foods.util.DisplayUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EatTimer extends BukkitRunnable {

    private final FoodsPlugin foodsPlugin;

    private final Food food;
    private final Player eater;
    private final Location initialLocation;

    private final int totalTicks;
    private int ticksPassed;

    private boolean done;

    public EatTimer(Food food, Player eater, FoodsPlugin foodsPlugin) {
        this.foodsPlugin = foodsPlugin;
        this.food = food;
        this.eater = eater;
        this.initialLocation = eater.getLocation();
        this.totalTicks = food.getConsumptionTime() * 20;
        this.ticksPassed = 0;
        this.done = false;
        this.eater.setWalkSpeed(0.1F);
        runTaskTimer(this.foodsPlugin, 0L, 1L);
    }

    @Override
    public void run() {
        if (!eater.isOnline() || eater.isDead() || !eater.getInventory().getItemInMainHand().isSimilar(food.getItem()) ||
                !food.getMovementPermission() && (Math.abs(initialLocation.getX() - eater.getLocation().getX()) > 2.0D ||
                Math.abs(initialLocation.getY() - eater.getLocation().getY()) > 2.0D ||
                Math.abs(initialLocation.getZ() - eater.getLocation().getZ()) > 2.0D)) {
            eater.setWalkSpeed(0.2F);

            eater.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("stopped-eating-food")
                            .replace("%food%", food.getItem().getItemMeta().getDisplayName()));
            DisplayUtils.actionbarMessage(eater, "");
            done = true;
            cancel();
            return;
        }
        if (ticksPassed >= totalTicks) {
            eater.setWalkSpeed(0.2F);

            DisplayUtils.actionbarMessage(eater, foodsPlugin.getMessageManager().getMessage("eating-food")
                    .replace("%food%", food.getItem().getItemMeta().getDisplayName())
                    .replace("%percent%", "100%"));
            eater.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("Ate Food").replace("%food%",
                            food.getItem().getItemMeta().getDisplayName()));

            eater.setFoodLevel(Math.min(eater.getFoodLevel() + food.getHunger(), 20));
            eater.setSaturation((float) (eater.getSaturation() + food.getHunger()*0.5 + 2.0));

            ItemUtils.consumeItem(eater, food.getItem(), 1);

            done = true;
            cancel();
            return;
        }
        DisplayUtils.actionbarMessage(eater, foodsPlugin.getMessageManager().getMessage("eating-food")
                .replace("%food%", food.getItem().getItemMeta().getDisplayName())
                .replace("%percent%", ticksPassed*100/totalTicks + "%"));
        ticksPassed += 1;
        done = false;
    }

    public boolean isDone() {
        return done;
    }

    public Player getEater() {
        return eater;
    }
}