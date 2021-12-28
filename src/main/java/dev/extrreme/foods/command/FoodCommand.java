package dev.extrreme.foods.command;

import dev.extrreme.foods.FoodsPlugin;
import dev.extrreme.foods.gui.RecipePreview;
import dev.extrreme.foods.Food;
import dev.extrreme.foods.util.GeneralUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public class FoodCommand implements CommandExecutor {

    private final FoodsPlugin foodsPlugin;

    public FoodCommand(FoodsPlugin foodsPlugin) {
        this.foodsPlugin = foodsPlugin;
    }

    @Override
    public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
                    "Command Reference:");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + "/food" + ChatColor.DARK_GRAY +
                    " - " + ChatColor.GRAY + "Shows command reference.");
            sender.sendMessage(ChatColor.GREEN + "/food list" + ChatColor.DARK_GRAY +
                    " - " + ChatColor.GRAY + "Get a list of all foods.");
            sender.sendMessage(ChatColor.GREEN + "/food recipe <food>" + ChatColor.DARK_GRAY +
                    " - " + ChatColor.GRAY + "Display the recipe for a given fooFd.");
            if (sender.hasPermission("foods.give")) {
                sender.sendMessage(ChatColor.GREEN + "/food give <player> <food> [<amount>]" + ChatColor.DARK_GRAY +
                        " - " + ChatColor.GRAY + "Give a player the specified food.");
            }
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + "Foods " + ChatColor.GRAY + "developed by " + ChatColor.GREEN +
                    "Extrreme" + ChatColor.GRAY + ".");
            sender.sendMessage("");
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            foodsPlugin.getFoodsMenu().open(((Player) sender), 0);
        }
        else if (args[0].equalsIgnoreCase("recipe")) {
            if (!(sender instanceof Player)) {
                return true;
            }

            Player player = (Player) sender;

            if (args.length < 2) {
                sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("arguments-error"));
                return true;
            }

            Food food = foodsPlugin.getFoodManager().getFoodFromString(args[1]);
            if (food == null) {
                sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("food-not-found"));
                return true;
            }

            new RecipePreview(food.getRecipe(), foodsPlugin).open(player);
        }
        else if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("foods.give")) {
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("arguments-error"));
                return true;
            }
            if (foodsPlugin.getServer().getPlayer(args[1]) == null) {
                sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("player-not-found").replace("%player%", args[1]));
                return true;
            }

            Player target = foodsPlugin.getServer().getPlayer(args[1]);
            Food food = foodsPlugin.getFoodManager().getFoodFromString(args[2]);
            if (food == null) {
                sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("food-not-found"));
                return true;
            }

            int multiplier = 1;
            if (args.length > 3 && GeneralUtils.isInteger(args[3])) {
                multiplier = Integer.parseInt(args[3]);
            }

            ItemStack foodItem = food.getItem().clone();
            foodItem.setAmount(foodItem.getAmount()*multiplier);
            target.getInventory().addItem(foodItem);

            sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("food-given")
                            .replace("%amount%", Integer.toString(multiplier))
                            .replace("%food%", food.getName())
                            .replace("%target%", target.getName()));
            target.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("food-received")
                            .replace("%amount%", Integer.toString(multiplier))
                            .replace("%food%", food.getName()));
        }
        else {
            sender.sendMessage(foodsPlugin.getMessageManager().getMessageWithPrefix("arguments-error"));
        }
        return true;
    }
}
