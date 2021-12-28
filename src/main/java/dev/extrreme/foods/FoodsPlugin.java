package dev.extrreme.foods;

import dev.extrreme.foods.config.MessageManager;
import dev.extrreme.foods.gui.FoodsMenu;
import dev.extrreme.foods.listener.FoodListener;
import dev.extrreme.foods.listener.GuiListener;
import dev.extrreme.foods.command.FoodCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoodsPlugin extends JavaPlugin {

    private MessageManager messageManager;
    private FoodManager foodManager;

    private FoodsMenu foodsMenu;

    @Override
    public void onEnable() {
        messageManager = new MessageManager(this);
        foodManager = new FoodManager(this);
        foodsMenu = new FoodsMenu(this);

        getServer().getPluginManager().registerEvents(new FoodListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        getCommand("food").setExecutor(new FoodCommand(this));
    }

    @Override
    public void onDisable() {}

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public FoodManager getFoodManager() {
        return foodManager;
    }

    public FoodsMenu getFoodsMenu() {
        return foodsMenu;
    }
}
