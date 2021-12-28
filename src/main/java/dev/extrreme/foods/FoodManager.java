package dev.extrreme.foods;

import dev.extrreme.foods.config.FileManager;
import dev.extrreme.foods.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FoodManager extends FileManager{

    private final FoodsPlugin foodsPlugin;

    private final List<Food> foods;
    private final List<EatTimer> activeTimers;

    public FoodManager(FoodsPlugin foodsPlugin) {
        super(foodsPlugin, "foods.yml", false);
        this.foodsPlugin = foodsPlugin;
        this.foods = new ArrayList<>();
        this.activeTimers = new ArrayList<>();
        load();
        this.foodsPlugin.getServer().getScheduler().runTaskTimer(this.foodsPlugin, this::tick, 5L, 5L);
    }

    public void load() {
        if (!file.exists()) {
            generateDefaults();
            save();
        }
        for (String foodName : configuration.getKeys(false)) {
            int hunger = configuration.getInt(foodName + ".hunger");
            int consumptionTime = configuration.getInt(foodName + ".consumption-time");
            boolean allowMovement = configuration.getBoolean(foodName + ".allow-movement");

            String itemType = configuration.getString(foodName + ".item-type");
            ItemStack item;

            if (itemType == null) {
                continue;
            }
            if (itemType.equalsIgnoreCase("HEAD")) {
                UUID identifier;
                if (configuration.getString(foodName + ".uuid") != null) {
                    identifier = UUID.fromString(configuration.getString(foodName + ".uuid"));
                } else {
                    identifier = UUID.randomUUID();
                    configuration.set(foodName + ".uuid", identifier.toString());
                    try {
                        configuration.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                item = ItemUtils.createCustomSkull(configuration.getString(foodName + ".texture"), identifier);
            }
            else if (itemType.equalsIgnoreCase("ITEM")) {
                item = new ItemStack(Material.getMaterial(configuration.getString(foodName + ".material")),
                        1, (short) configuration.getInt(foodName + ".durability"));

                if (configuration.getBoolean(foodName + ".unbreakable")) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    item.setItemMeta(meta);
                }
            }
            else {
                continue;
            }

            item.setAmount(configuration.getInt(foodName + ".amount"));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    configuration.getString(foodName + ".name")));
            List<String> lore = configuration.getStringList(foodName + ".lore");
            meta.setLore(lore.stream().map(loreLine -> ChatColor.translateAlternateColorCodes('&', loreLine
                    .replace("%hunger%", Integer.toString(hunger))
                    .replace("%consume time%", Integer.toString(consumptionTime)))).collect(Collectors.toList()));
            item.setItemMeta(meta);

            String recipeType = configuration.getString(foodName + ".craft-method", "");
            Recipe recipe = null;

            if (recipeType.equalsIgnoreCase("CRAFTING TABLE")) {
                recipe = ItemUtils.createCraftingRecipe(foodName, item,
                        configuration.getStringList(foodName + ".recipe"), foodsPlugin);
            }
            else if (recipeType.equalsIgnoreCase("FURNACE")) {
                recipe = ItemUtils.createFurnaceRecipe(item, configuration.getString(foodName + ".furnace-input"),
                        foodsPlugin);
            }

            foods.add(new Food(foodName, hunger, consumptionTime, allowMovement, item, recipe));
        }
    }


    public void generateDefaults() {
        configuration.set("Toast.name", "&eToast");
        configuration.set("Toast.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Toast.hunger", 4);
        configuration.set("Toast.consumption-time", 2);
        configuration.set("Toast.allow-movement", true);
        configuration.set("Toast.item-type", "HEAD");
        configuration.set("Toast.texture", "b16ccae580b9179ff536eb07785b087c725d92f6e52bae7510516f3ca9dae429");
        configuration.set("Toast.amount", 3);
        configuration.set("Toast.craft-method", "FURNACE");
        configuration.set("Toast.furnace-input", "BREAD");

        configuration.set("Mac_N_Cheese.name", "&6Mac n' Cheese");
        configuration.set("Mac_N_Cheese.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Mac_N_Cheese.hunger", 12);
        configuration.set("Mac_N_Cheese.consumption-time", 6);
        configuration.set("Mac_N_Cheese.allow-movement", false);
        configuration.set("Mac_N_Cheese.item-type", "HEAD");
        configuration.set("Mac_N_Cheese.texture", "6bbecba5231805aaadda81d764b096eee62ed2e4cb447448544f5182b091f101");
        configuration.set("Mac_N_Cheese.amount", 1);
        configuration.set("Mac_N_Cheese.craft-method", "CRAFTING TABLE");
        configuration.set("Mac_N_Cheese.recipe", Arrays.asList("N/A", "N/A", "N/A", "STICK", "INK_SACK:11", "MILK_BUCKET", "N/A", "BOWL", "N/A"));

        configuration.set("Hotdog.name", "&eHotdog");
        configuration.set("Hotdog.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Hotdog.hunger", 12);
        configuration.set("Hotdog.consumption-time", 6);
        configuration.set("Hotdog.allow-movement", true);
        configuration.set("Hotdog.item-type", "HEAD");
        configuration.set("Hotdog.texture", "15a995f41c4630e879f34dbac41f92317558534f2d4d7a468b670e1dc52177");
        configuration.set("Hotdog.amount", 1);
        configuration.set("Hotdog.craft-method", "CRAFTING TABLE");
        configuration.set("Hotdog.recipe", Arrays.asList("N/A", "N/A", "N/A", "BREAD", "GRILLED_PORK", "BREAD", "N/A", "N/A", "N/A"));

        configuration.set("Hamburger.name", "&eHamburger");
        configuration.set("Hamburger.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Hamburger.hunger", 16);
        configuration.set("Hamburger.consumption-time", 8);
        configuration.set("Hamburger.allow-movement", false);
        configuration.set("Hamburger.item-type", "HEAD");
        configuration.set("Hamburger.texture", "add0b236fd2ccce103d05b794445da4745816d4c5a19ea9e4067b2e00adac041");
        configuration.set("Hamburger.amount", 1);
        configuration.set("Hamburger.craft-method", "CRAFTING TABLE");
        configuration.set("Hamburger.recipe", Arrays.asList("N/A", "BREAD", "N/A", "LEAVES", "COOKED_BEEF", "BEETROOT", "N/A", "BREAD", "N/A"));

        configuration.set("Cheeseburger.name", "&eCheeseburger");
        configuration.set("Cheeseburger.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Cheeseburger.hunger", 16);
        configuration.set("Cheeseburger.consumption-time", 8);
        configuration.set("Cheeseburger.allow-movement", false);
        configuration.set("Cheeseburger.item-type", "HEAD");
        configuration.set("Cheeseburger.texture", "6ae9c9bc898c7cfe9a3b11b6deaed1757817ad02511ab5a8b306ab92eeb3e2d");
        configuration.set("Cheeseburger.amount", 1);
        configuration.set("Cheeseburger.craft-method", "CRAFTING TABLE");
        configuration.set("Cheeseburger.recipe", Arrays.asList("N/A", "BREAD", "N/A", "MILK_BUCKET", "COOKED_BEEF", "MILK_BUCKET", "N/A", "BREAD", "N/A"));

        configuration.set("Krabby_Patty.name", "&bKrabby Patty");
        configuration.set("Krabby_Patty.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Krabby_Patty.hunger", 16);
        configuration.set("Krabby_Patty.consumption-time", 8);
        configuration.set("Krabby_Patty.allow-movement", false);
        configuration.set("Krabby_Patty.item-type", "HEAD");
        configuration.set("Krabby_Patty.texture", "eee32de5918d41ad179b3ccc63e0fb238ad68e2fe6b650dfde1950ebf928eb");
        configuration.set("Krabby_Patty.amount", 1);
        configuration.set("Krabby_Patty.craft-method", "CRAFTING TABLE");
        configuration.set("Krabby_Patty.recipe", Arrays.asList("N/A", "BREAD", "N/A", "LEAVES", "COOKED_FISH", "BEETROOT", "N/A", "BREAD", "N/A"));

        configuration.set("Taco.name", "&eTaco");
        configuration.set("Taco.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Taco.hunger", 12);
        configuration.set("Taco.consumption-time", 6);
        configuration.set("Taco.allow-movement", false);
        configuration.set("Taco.item-type", "HEAD");
        configuration.set("Taco.texture", "1ad7c0a04f1485c7a3ef261a48ee83b2f1aa701ab11f3fc911e0366a9b97e");
        configuration.set("Taco.amount", 1);
        configuration.set("Taco.craft-method", "CRAFTING TABLE");
        configuration.set("Taco.recipe", Arrays.asList("BEETROOT", "COOKED_BEEF", "LEAVES", "WHEAT", "COOKED_BEEF", "WHEAT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Burrito.name", "&eBurrito");
        configuration.set("Burrito.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Burrito.hunger", 12);
        configuration.set("Burrito.consumption-time", 6);
        configuration.set("Burrito.allow-movement", false);
        configuration.set("Burrito.item-type", "HEAD");
        configuration.set("Burrito.texture", "a387a621e266186e60683392eb274ebb225b04868ab959177d9dc181d8f286");
        configuration.set("Burrito.amount", 1);
        configuration.set("Burrito.craft-method", "CRAFTING TABLE");
        configuration.set("Burrito.recipe", Arrays.asList("WHEAT", "WHEAT", "WHEAT", "LEAVES", "COOKED_BEEF", "BROWN_MUSHROOM", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Cheese_Pizza.name", "&ePizza &7(&eCheese&7)");
        configuration.set("Cheese_Pizza.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Cheese_Pizza.hunger", 12);
        configuration.set("Cheese_Pizza.consumption-time", 6);
        configuration.set("Cheese_Pizza.allow-movement", false);
        configuration.set("Cheese_Pizza.item-type", "HEAD");
        configuration.set("Cheese_Pizza.texture", "f1a689e7bfcf040575cba68cc36149203bfd713c3b81088739220ea5b7b7f9b9");
        configuration.set("Cheese_Pizza.amount", 1);
        configuration.set("Cheese_Pizza.craft-method", "CRAFTING TABLE");
        configuration.set("Cheese_Pizza.recipe", Arrays.asList("N/A", "N/A", "N/A", "BEETROOT", "MILK_BUCKET", "BEETROOT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Margherita_Pizza.name", "&ePizza &7(&aMargherita&7)");
        configuration.set("Margherita_Pizza.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Margherita_Pizza.hunger", 20);
        configuration.set("Margherita_Pizza.consumption-time", 10);
        configuration.set("Margherita_Pizza.allow-movement", false);
        configuration.set("Margherita_Pizza.item-type", "HEAD");
        configuration.set("Margherita_Pizza.texture", "f6aa4527eeb2f2bce54ba64208cb58dee8fd3b2f06b823ea2bf1a721c92354c");
        configuration.set("Margherita_Pizza.amount", 1);
        configuration.set("Margherita_Pizza.craft-method", "CRAFTING TABLE");
        configuration.set("Margherita_Pizza.recipe", Arrays.asList("N/A", "LEAVES", "N/A", "BEETROOT", "MILK_BUCKET", "BEETROOT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Pepperoni_Pizza.name", "&ePizza &7(&cPepperoni&7)");
        configuration.set("Pepperoni_Pizza.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Pepperoni_Pizza.hunger", 20);
        configuration.set("Pepperoni_Pizza.consumption-time", 10);
        configuration.set("Pepperoni_Pizza.allow-movement", false);
        configuration.set("Pepperoni_Pizza.item-type", "HEAD");
        configuration.set("Pepperoni_Pizza.texture", "21d9d2301c60d5cd8949da664fd5c63bd5e5713116183b66318a1fc99ab0b1e4");
        configuration.set("Pepperoni_Pizza.amount", 1);
        configuration.set("Pepperoni_Pizza.craft-method", "CRAFTING TABLE");
        configuration.set("Pepperoni_Pizza.recipe", Arrays.asList("GRILLED_PORK", "GRILLED_PORK", "GRILLED_PORK", "BEETROOT", "MILK_BUCKET", "BEETROOT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("MeatLover_Pizza.name", "&ePizza &7(&cMeat Lover&7)");
        configuration.set("MeatLover_Pizza.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("MeatLover_Pizza.hunger", 20);
        configuration.set("MeatLover_Pizza.consumption-time", 10);
        configuration.set("MeatLover_Pizza.allow-movement", false);
        configuration.set("MeatLover_Pizza.item-type", "HEAD");
        configuration.set("MeatLover_Pizza.texture", "65711773d0b7d30af1c24e9aa5a0dcb788e9ed8b7cf73775c266780bc0089d97");
        configuration.set("MeatLover_Pizza.amount", 1);
        configuration.set("MeatLover_Pizza.craft-method", "CRAFTING TABLE");
        configuration.set("MeatLover_Pizza.recipe", Arrays.asList("PORK", "COOKED_BEEF", "COOKED_CHICKEN", "BEETROOT", "MILK_BUCKET", "BEETROOT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Vegetarian_Pizza.name", "&ePizza &7(&2Vegetarian&7)");
        configuration.set("Vegetarian_Pizza.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Vegetarian_Pizza.hunger", 15);
        configuration.set("Vegetarian_Pizza.consumption-time", 7.5);
        configuration.set("Vegetarian_Pizza.allow-movement", false);
        configuration.set("Vegetarian_Pizza.item-type", "HEAD");
        configuration.set("Vegetarian_Pizza.texture", "bb2b28f673190d04e2af5a5ad21c959c166785116ffe994f43389428cf93d392");
        configuration.set("Vegetarian_Pizza.amount", 1);
        configuration.set("Vegetarian_Pizza.craft-method", "CRAFTING TABLE");
        configuration.set("Vegetarian_Pizza.recipe", Arrays.asList("LEAVES", "LEAVES", "LEAVES", "BEETROOT", "MILK_BUCKET", "BEETROOT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Pineapple_Pizza.name", "&ePizza &7(&6Pineapple&7)");
        configuration.set("Pineapple_Pizza.lore", Arrays.asList("&7Absolutely disgusting", "&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Pineapple_Pizza.hunger", 15);
        configuration.set("Pineapple_Pizza.consumption-time", 7.5);
        configuration.set("Pineapple_Pizza.allow-movement", false);
        configuration.set("Pineapple_Pizza.item-type", "HEAD");
        configuration.set("Pineapple_Pizza.texture", "22bd4dacf9f5c59ef34880c32c1526e489dd739df461eb5beeb8d36f8ab55b64");
        configuration.set("Pineapple_Pizza.amount", 1);
        configuration.set("Pineapple_Pizza.craft-method", "CRAFTING TABLE");
        configuration.set("Pineapple_Pizza.recipe", Arrays.asList("LEAVES", "GOLD_INGOT", "APPLE", "BEETROOT", "MILK_BUCKET", "BEETROOT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("California_Sushi.name", "&2Sushi &7(&aCalifornia Roll&7)");
        configuration.set("California_Sushi.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("California_Sushi.hunger", 2);
        configuration.set("California_Sushi.consumption-time", 1);
        configuration.set("California_Sushi.allow-movement", true);
        configuration.set("California_Sushi.item-type", "HEAD");
        configuration.set("California_Sushi.texture", "a5c63312bb42986751460121cd3e1e7675640d9d81775f413bddb389fbb27bb");
        configuration.set("California_Sushi.amount", 3);
        configuration.set("California_Sushi.craft-method", "CRAFTING TABLE");
        configuration.set("California_Sushi.recipe", Arrays.asList("INK_SACK:15", "INK_SACK:2", "SEEDS", "CARROT_ITEM", "COOKED_FISH:1", "SLIME_BALL", "SEEDS", "INK_SACK:2", "INK_SACK:15"));

        configuration.set("Sesame_Sushi.name", "&2Sushi &7(&6Sesame&7)");
        configuration.set("Sesame_Sushi.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Sesame_Sushi.hunger", 5);
        configuration.set("Sesame_Sushi.consumption-time", 2);
        configuration.set("Sesame_Sushi.allow-movement", true);
        configuration.set("Sesame_Sushi.item-type", "HEAD");
        configuration.set("Sesame_Sushi.texture", "90ec585fa3044ccbc94441c8a79b4b8c23eaa6ddb4d519949826c51ef335059");
        configuration.set("Sesame_Sushi.amount", 3);
        configuration.set("Sesame_Sushi.craft-method", "CRAFTING TABLE");
        configuration.set("Sesame_Sushi.recipe", Arrays.asList("DOUBLE_PLANT", "INK_SACK:2", "SEEDS", "INK_SACK:15", "COOKED_FISH", "CARROT_ITEM", "SEEDS", "INK_SACK:2", "DOUBLE_PLANT"));

        configuration.set("Shrimp_Sushi.name", "&2Sushi &7(&cShrimp Roll&7)");
        configuration.set("Shrimp_Sushi.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Shrimp_Sushi.hunger", 5);
        configuration.set("Shrimp_Sushi.consumption-time", 2);
        configuration.set("Shrimp_Sushi.allow-movement", true);
        configuration.set("Shrimp_Sushi.item-type", "HEAD");
        configuration.set("Shrimp_Sushi.texture", "ae9d22d9ada63e281420ae33691880869fa1a14bfdf87d8e538e998a8f29595b");
        configuration.set("Shrimp_Sushi.amount", 3);
        configuration.set("Shrimp_Sushi.craft-method", "CRAFTING TABLE");
        configuration.set("Shrimp_Sushi.recipe", Arrays.asList("N/A", "INK_SACK:2", "N/A", "INK_SACK:15", "RAW_FISH", "SUGAR_CANE", "N/A", "INK_SACK:2", "N/A"));

        configuration.set("Tuna_Sushi.name", "&2Sushi &7(&cTuna Roll&7)");
        configuration.set("Tuna_Sushi.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Tuna_Sushi.hunger", 5);
        configuration.set("Tuna_Sushi.consumption-time", 2);
        configuration.set("Tuna_Sushi.allow-movement", true);
        configuration.set("Tuna_Sushi.item-type", "HEAD");
        configuration.set("Tuna_Sushi.texture", "87ede9e4b6ebe363585f2390d3d67f4f4e415f544419ab73dae93b79815eb4b");
        configuration.set("Tuna_Sushi.amount", 3);
        configuration.set("Tuna_Sushi.craft-method", "CRAFTING TABLE");
        configuration.set("Tuna_Sushi.recipe", Arrays.asList("N/A", "INK_SACK:2", "N/A", "INK_SACK:15", "RAW_FISH:1", "INK_SACK:15", "N/A", "INK_SACK:2", "N/A"));

        configuration.set("Ikura_Sushi.name", "&2Sushi &7(&cIkura Roll&7)");
        configuration.set("Ikura_Sushi.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Ikura_Sushi.hunger", 5);
        configuration.set("Ikura_Sushi.consumption-time", 2);
        configuration.set("Ikura_Sushi.allow-movement", true);
        configuration.set("Ikura_Sushi.item-type", "HEAD");
        configuration.set("Ikura_Sushi.texture", "80f4aa94beef0d5ab19979fd4913f4d2825acb3b642b9fa1afc9ebb3f79");
        configuration.set("Ikura_Sushi.amount", 3);
        configuration.set("Ikura_Sushi.craft-method", "CRAFTING TABLE");
        configuration.set("Ikura_Sushi.recipe", Arrays.asList("N/A", "INK_SACK:2", "N/A", "EGG", "RAW_FISH:1", "EGG", "N/A", "INK_SACK:2", "N/A"));

        configuration.set("SalmonNigri_Sushi.name", "&2Sushi &7(&cSalmon Nigri&7)");
        configuration.set("SalmonNigri_Sushi.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("SalmonNigri_Sushi.hunger", 3);
        configuration.set("SalmonNigri_Sushi.consumption-time", 1);
        configuration.set("SalmonNigri_Sushi.allow-movement", true);
        configuration.set("SalmonNigri_Sushi.item-type", "HEAD");
        configuration.set("SalmonNigri_Sushi.texture", "23bf8fca2af3592c5574b13e3bcf61e2fae829788535f0ddeaa7a2e45b6ba4");
        configuration.set("SalmonNigri_Sushi.amount", 6);
        configuration.set("SalmonNigri_Sushi.craft-method", "CRAFTING TABLE");
        configuration.set("SalmonNigri_Sushi.recipe", Arrays.asList("N/A", "N/A", "N/A", "RAW_FISH:1", "INK_SACK:15", "RAW_FISH:1", "N/A", "N/A", "N/A"));

        configuration.set("Rice_Bowl.name", "&fBowl of Rice");
        configuration.set("Rice_Bowl.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Rice_Bowl.hunger", 8);
        configuration.set("Rice_Bowl.consumption-time", 6);
        configuration.set("Rice_Bowl.allow-movement", true);
        configuration.set("Rice_Bowl.item-type", "HEAD");
        configuration.set("Rice_Bowl.texture", "994c95414d927f7ed86a4ae18077df66798ea736ddb5cb49aad7699d91dd1ef2");
        configuration.set("Rice_Bowl.amount", 3);
        configuration.set("Rice_Bowl.craft-method", "CRAFTING TABLE");
        configuration.set("Rice_Bowl.recipe", Arrays.asList("N/A", "N/A", "N/A", "SUGAR_CANE", "INK_SACK:15", "SUGAR_CANE", "N/A", "BOWL", "N/A"));

        configuration.set("Fried_Chicken.name", "&eFried Chicken");
        configuration.set("Fried_Chicken.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Fried_Chicken.hunger", 20);
        configuration.set("Fried_Chicken.consumption-time", 12);
        configuration.set("Fried_Chicken.allow-movement", false);
        configuration.set("Fried_Chicken.item-type", "HEAD");
        configuration.set("Fried_Chicken.texture", "b8fb0502a3aa5f8bd32a5ea5e519c3dd353234170dfef959ee8adb9487fea");
        configuration.set("Fried_Chicken.amount", 1);
        configuration.set("Fried_Chicken.craft-method", "CRAFTING TABLE");
        configuration.set("Fried_Chicken.recipe", Arrays.asList("N/A", "N/A", "N/A", "COOKED_CHICKEN", "COOKED_CHICKEN", "COOKED_CHICKEN", "COOKED_CHICKEN", "BUCKET", "COOKED_CHICKEN"));

        configuration.set("French_Fries.name", "&eFrench Fries");
        configuration.set("French_Fries.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("French_Fries.hunger", 13);
        configuration.set("French_Fries.consumption-time", 7);
        configuration.set("French_Fries.allow-movement", true);
        configuration.set("French_Fries.item-type", "HEAD");
        configuration.set("French_Fries.texture", "505826b37a7f7b23c3ac9dda97f4744d120340d6fa44f937e2b79f8d395f");
        configuration.set("French_Fries.amount", 1);
        configuration.set("French_Fries.craft-method", "CRAFTING TABLE");
        configuration.set("French_Fries.recipe", Arrays.asList("N/A", "N/A", "N/A", "POTATO_ITEM", "POTATO_ITEM", "POTATO_ITEM", "POTATO_ITEM", "BUCKET", "POTATO_ITEM"));

        configuration.set("Chocolate_Donut.name", "&eDonut &7(&eChocolate&7)");
        configuration.set("Chocolate_Donut.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Chocolate_Donut.hunger", 6);
        configuration.set("Chocolate_Donut.consumption-time", 3);
        configuration.set("Chocolate_Donut.allow-movement", true);
        configuration.set("Chocolate_Donut.item-type", "HEAD");
        configuration.set("Chocolate_Donut.texture", "3ab04847f09b3f2e11fc412c2e241accf99675432933b471681005c42e5d2285");
        configuration.set("Chocolate_Donut.amount", 1);
        configuration.set("Chocolate_Donut.craft-method", "CRAFTING TABLE");
        configuration.set("Chocolate_Donut.recipe", Arrays.asList("SUGAR", "INK_SACK:3", "SUGAR", "WHEAT", "MILK_BUCKET", "WHEAT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Glazed_Donut.name", "&eDonut &7(&f&oGlazed&7)");
        configuration.set("Glazed_Donut.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Glazed_Donut.hunger", 6);
        configuration.set("Glazed_Donut.consumption-time", 3);
        configuration.set("Glazed_Donut.allow-movement", true);
        configuration.set("Glazed_Donut.item-type", "HEAD");
        configuration.set("Glazed_Donut.texture", "df5925ed50361f9b791dd082891118e60b9c3faed6775049ff2938be81310451");
        configuration.set("Glazed_Donut.amount", 1);
        configuration.set("Glazed_Donut.craft-method", "CRAFTING TABLE");
        configuration.set("Glazed_Donut.recipe", Arrays.asList("SUGAR", "SUGAR", "SUGAR", "WHEAT", "MILK_BUCKET", "WHEAT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("PinkFrosting_Donut.name", "&eDonut &7(&dPink Frosting&7)");
        configuration.set("PinkFrosting_Donut.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("PinkFrosting_Donut.hunger", 6);
        configuration.set("PinkFrosting_Donut.consumption-time", 3);
        configuration.set("PinkFrosting_Donut.allow-movement", true);
        configuration.set("PinkFrosting_Donut.item-type", "HEAD");
        configuration.set("PinkFrosting_Donut.texture", "837c9b82b186656e9f6363a2a1c6a4b5b93cfa9ef4dad6f16b94ebb5e362678");
        configuration.set("PinkFrosting_Donut.amount", 1);
        configuration.set("PinkFrosting_Donut.craft-method", "CRAFTING TABLE");
        configuration.set("PinkFrosting_Donut.recipe", Arrays.asList("SUGAR", "INK_SACK:9", "SUGAR", "WHEAT", "MILK_BUCKET", "WHEAT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("WhiteFrosting_Donut.name", "&eDonut &7(&fWhite Frosting&7)");
        configuration.set("WhiteFrosting_Donut.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("WhiteFrosting_Donut.hunger", 6);
        configuration.set("WhiteFrosting_Donut.consumption-time", 3);
        configuration.set("WhiteFrosting_Donut.allow-movement", true);
        configuration.set("WhiteFrosting_Donut.item-type", "HEAD");
        configuration.set("WhiteFrosting_Donut.texture", "d07b8c51acec2a508bb2fa652fb6e4a08b19485159a099f5982ccb88df1fe27e");
        configuration.set("WhiteFrosting_Donut.amount", 1);
        configuration.set("WhiteFrosting_Donut.craft-method", "CRAFTING TABLE");
        configuration.set("WhiteFrosting_Donut.recipe", Arrays.asList("SUGAR", "INK_SACK:15", "SUGAR", "WHEAT", "MILK_BUCKET", "WHEAT", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Chocolate_Cupcake.name", "&eCupcake &7(&eChocolate&7)");
        configuration.set("Chocolate_Cupcake.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Chocolate_Cupcake.hunger", 6);
        configuration.set("Chocolate_Cupcake.consumption-time", 3);
        configuration.set("Chocolate_Cupcake.allow-movement", true);
        configuration.set("Chocolate_Cupcake.item-type", "HEAD");
        configuration.set("Chocolate_Cupcake.texture", "e5716f4080848aa4b79da3706b94e7b72fe4b1bd64938c08cd5c03ae3b73599");
        configuration.set("Chocolate_Cupcake.amount", 1);
        configuration.set("Chocolate_Cupcake.craft-method", "CRAFTING TABLE");
        configuration.set("Chocolate_Cupcake.recipe", Arrays.asList("MILK_BUCKET", "INK_SACK:3", "MILK_BUCKET", "SUGAR", "EGG", "SUGAR", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("WhiteFrosting_Cupcake.name", "&eCupcake &7(&fWhite Frosting&7)");
        configuration.set("WhiteFrosting_Cupcake.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("WhiteFrosting_Cupcake.hunger", 6);
        configuration.set("WhiteFrosting_Cupcake.consumption-time", 3);
        configuration.set("WhiteFrosting_Cupcake.allow-movement", true);
        configuration.set("WhiteFrosting_Cupcake.item-type", "HEAD");
        configuration.set("WhiteFrosting_Cupcake.texture", "e2f5638c213ea2a5a94e6598166bc47eeeb40f5df84da70cd9233c1812df110");
        configuration.set("WhiteFrosting_Cupcake.amount", 1);
        configuration.set("WhiteFrosting_Cupcake.craft-method", "CRAFTING TABLE");
        configuration.set("WhiteFrosting_Cupcake.recipe", Arrays.asList("MILK_BUCKET", "INK_SACK:15", "MILK_BUCKET", "SUGAR", "EGG", "SUGAR", "WHEAT", "WHEAT", "WHEAT"));

        configuration.set("Apple_Pie.name", "&ePie &7(&cApple&7)");
        configuration.set("Apple_Pie.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Apple_Pie.hunger", 13);
        configuration.set("Apple_Pie.consumption-time", 7);
        configuration.set("Apple_Pie.allow-movement", false);
        configuration.set("Apple_Pie.item-type", "HEAD");
        configuration.set("Apple_Pie.texture", "d53c1e87e537f1ab2774ddafb83439b336f4a777b47ad82bcb30d5fcbdf9bc");
        configuration.set("Apple_Pie.amount", 1);
        configuration.set("Apple_Pie.craft-method", "CRAFTING TABLE");
        configuration.set("Apple_Pie.recipe", Arrays.asList("WHEAT", "WHEAT", "WHEAT", "APPLE", "SUGAR", "APPLE", "WHEAT", "EGG", "WHEAT"));

        configuration.set("Twinkie.name", "&fTwinkie");
        configuration.set("Twinkie.lore", Arrays.asList("&2Hunger Replenished: &a%hunger%", "&2Consume Time: &a%consume time% seconds"));
        configuration.set("Twinkie.hunger", 8);
        configuration.set("Twinkie.consumption-time", 4);
        configuration.set("Twinkie.allow-movement", true);
        configuration.set("Twinkie.item-type", "HEAD");
        configuration.set("Twinkie.texture", "ca75fd7fec8959ba23c29cdd525f795991a22a6ef478d21c1a3f741394ed6082");
        configuration.set("Twinkie.amount", 1);
        configuration.set("Twinkie.craft-method", "CRAFTING TABLE");
        configuration.set("Twinkie.recipe", Arrays.asList("WHEAT", "WHEAT", "WHEAT", "WHEAT", "INK_SACK:15", "WHEAT", "WHEAT", "SUGAR", "WHEAT"));
    }

    private void tick() {
        activeTimers.removeIf(EatTimer::isDone);
    }

    public List<Food> getFoods() {
        return foods;
    }

    public List<EatTimer> getActiveTimers() {
        return activeTimers;
    }

    public boolean isEating(Player player) {
        for (EatTimer timer : activeTimers) {
            if (timer.getEater() != player || timer.isDone()) {
                continue;
            }
            return true;
        }
        return false;
    }

    public Food getFoodFromItem(ItemStack item) {
        for (Food food : foods) {
            if (!food.getItem().isSimilar(item)) {
                continue;
            }
            return food;
        }
        return null;
    }

    public Food getFoodFromString(String foodName) {
        for (Food food : foodsPlugin.getFoodManager().getFoods()) {
            if (!food.getName().equalsIgnoreCase(foodName)) {
                continue;
            }
            return food;
        }
        return null;
    }
}
