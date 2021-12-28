package dev.extrreme.foods.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    public static void consumeItem(Player player, ItemStack item, int count) {
        for (ItemStack invItem : player.getInventory()) {
            if (item == null || invItem == null) {
                continue;
            }
            if (!(invItem.isSimilar(item))) {
                continue;
            }
            if (invItem.getAmount() < count) {
                invItem.setType(Material.AIR);
                break;
            }

            invItem.setAmount(invItem.getAmount() - count);
            break;
        }
        player.updateInventory();
    }

    public static ItemStack createCustomSkull(String texture, UUID uuid) {
        texture = "https://textures.minecraft.net/texture/" + texture;

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        GameProfile profile = new GameProfile(uuid, null);

        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ShapedRecipe createCraftingRecipe(String name, ItemStack item, List<String> recipeIngredients, JavaPlugin plugin) {
        char[] recipeLetters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};

        for (int x = 0; x < recipeIngredients.size(); x++) {
            if (recipeIngredients.get(x).equals("N/A")) {
                recipeLetters[x] = ' ';
            }
        }

        StringBuilder row1 = new StringBuilder();
        StringBuilder row2 = new StringBuilder();
        StringBuilder row3 = new StringBuilder();

        for (int x = 0; x < recipeLetters.length; x++) {
            if (x < 3) {
                row1.append(recipeLetters[x]);
            } else if (x < 6) {
                row2.append(recipeLetters[x]);
            } else {
                row3.append(recipeLetters[x]);
            }
        }

        String[] recipeShape = {row1.toString(), row2.toString(), row3.toString()};
        NamespacedKey key = new NamespacedKey(plugin, name);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape(recipeShape);

        for (int x = 0; x < recipeLetters.length; x++) {
            String recipeIngredient = recipeIngredients.get(x);

            if (recipeIngredient.equals("N/A")) {
                continue;
            }

            Material material;
            int data = 0;

            if (recipeIngredient.contains(":") && GeneralUtils.isInteger(recipeIngredient.split(":")[1])) {
                material = Material.getMaterial(recipeIngredient.split(":")[0]);
                data = Integer.parseInt(recipeIngredient.split(":")[1]);
            } else {
                material = Material.valueOf(recipeIngredients.get(x));
            }

            recipe.setIngredient(recipeLetters[x], material, data);
        }
        plugin.getServer().addRecipe(recipe);
        return recipe;
    }

    public static FurnaceRecipe createFurnaceRecipe(ItemStack item, String smelted, JavaPlugin plugin) {
        Material mat = Material.getMaterial(smelted);
        if (mat == null) {
            return null;
        }
        FurnaceRecipe recipe = new FurnaceRecipe(item, mat);
        plugin.getServer().addRecipe(recipe);

        return recipe;
    }
}
