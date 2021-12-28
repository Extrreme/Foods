package dev.extrreme.foods.gui;

import dev.extrreme.foods.FoodsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FoodsMenu {

    private final ArrayList<Inventory> pages;

    public FoodsMenu(FoodsPlugin foodsPlugin) {
        pages = new ArrayList<>();

        int numPages = (foodsPlugin.getFoodManager().getFoods().size()+35)/36;
        if (numPages < 1) {
            numPages = 1;
        }
        int pos = 0;

        for (int i = 0; i < numPages; i++) {
            Inventory page = foodsPlugin.getServer().createInventory(null, 54,
                    ChatColor.GRAY + "Foods " + ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + (i+1));
            applyTemplate(page, i==0, (i+1)==numPages, i+1);
            for (int j = i*36; j < foodsPlugin.getFoodManager().getFoods().size() && j < (i+1)*36; j++, pos++) {
                ItemStack item = foodsPlugin.getFoodManager().getFoods().get(pos).getItem().clone();
                item.setAmount(1);
                page.setItem(pos-(i*36)+9, item);
            }
            fill(page);
            pages.add(page);
        }
        if (pages.isEmpty()) {
            Inventory page = foodsPlugin.getServer().createInventory(null, 54,
                    ChatColor.GRAY + "Foods " +ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + 1);
            applyTemplate(page, true, true, 1);
            fill(page);
            pages.add(page);
        }
    }

    public void open(Player player, int page) {
        player.openInventory(pages.get(page));
    }

    private void applyTemplate(Inventory page, boolean first, boolean last, int pageNum) {
        ItemStack fillerGlass = getFillerGlass(15);
        for (int i = 0; i < 9; i++) {
            page.setItem(i, fillerGlass);
        }
        for (int i = 45; i < 54; i++) {
            page.setItem(i, fillerGlass);
        }
        if (!last) {
            ItemStack next = new ItemStack(Material.DOUBLE_PLANT);
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.setDisplayName(ChatColor.DARK_GREEN + "Next");
            nextMeta.setLore(Collections.singletonList(ChatColor.GREEN + "Click to go to the next page."));
            next.setItemMeta(nextMeta);
            page.setItem(50, next);
        }
        if (!first) {
            ItemStack back = new ItemStack(Material.DOUBLE_PLANT);
            ItemMeta backMeta = back.getItemMeta();
            backMeta.setDisplayName(ChatColor.DARK_GREEN + "Back");
            backMeta.setLore(Collections.singletonList(ChatColor.GREEN + "Click to go to the previous page."));
            back.setItemMeta(backMeta);
            page.setItem(48, back);
        }
        ItemStack pageNumItem = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageNumItem.getItemMeta();
        pageMeta.setDisplayName(ChatColor.GREEN + "Page " + pageNum);
        pageNumItem.setItemMeta(pageMeta);
        page.setItem(49, pageNumItem);
    }

    private void fill(Inventory page) {
        ItemStack fillerGlass = getFillerGlass(7);
        for (int i = 0; i < page.getSize(); i++) {
            if (page.getItem(i) != null) {
                continue;
            }
            page.setItem(i, fillerGlass);
        }
    }

    private ItemStack getFillerGlass(int color) {
        ItemStack fillerGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) color);
        ItemMeta fillerGlassMeta = fillerGlass.getItemMeta();
        fillerGlassMeta.setDisplayName(ChatColor.BLACK + "");
        fillerGlass.setItemMeta(fillerGlassMeta);
        return fillerGlass;
    }
}
