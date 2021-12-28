package dev.extrreme.foods.config;

import dev.extrreme.foods.FoodsPlugin;
import org.bukkit.ChatColor;

public class MessageManager extends FileManager{


    public MessageManager(FoodsPlugin instance) {
        super(instance, "messages.yml", true);
    }

    public void load() {
        if (!file.exists()) {
            configuration.set("prefix", "&8[&aFoods&8]");

            configuration.set("arguments-error", "&7Error, please check your arguments and try again.");
            configuration.set("player-not-found", "&7Error, cannot find &a%player%&7.");
            configuration.set("no-permission", "&7Error, you do not have permission for this command.");

            configuration.set("food-not-found", "&7Error, the food you specified was not found.");

            configuration.set("started-eating-food", "&7You are eating %food%&7.");
            configuration.set("eating-food", "&7Eating %food%&7 (&a%percent%&7).");
            configuration.set("stopped-eating-food", "&7You have stopped eating %food%&7.");
            configuration.set("ate-food", "&7You ate %food%&7.");

            configuration.set("not-hungry", "&7You are not hungry!");

            configuration.set("food-given", "&7You have given &a%amount%x %food%&7 to &a%target%");
            configuration.set("food-received", "&7You have been given &a%amount%x %food%");

            save();
        }
    }

    public String getMessageWithPrefix(String path) {
        return getMessage("prefix") + " " + getMessage(path);
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                configuration.getString(path));
    }
}
