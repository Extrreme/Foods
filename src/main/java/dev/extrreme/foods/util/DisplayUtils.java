package dev.extrreme.foods.util;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class DisplayUtils {

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (title == null && subtitle == null) {
            return;
        }

        PacketPlayOutTitle resetPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, null);
        PacketPlayOutTitle timePacket = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        PacketPlayOutTitle titlePacket = null;
        PacketPlayOutTitle subtitlePacket = null;

        if (title != null) {
            IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', title) + "\"}");
            titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent);
        }
        if (subtitle != null) {
            IChatBaseComponent subtitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', subtitle) + "\"}");
            subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleComponent);
        }

        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;

        connection.sendPacket(resetPacket);
        connection.sendPacket(timePacket);

        if (titlePacket != null) {
            connection.sendPacket(titlePacket);
        }
        if (subtitlePacket != null) {
            connection.sendPacket(subtitlePacket);
        }

    }

    public static void actionbarMessage(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)), ChatMessageType.GAME_INFO);
        (((CraftPlayer)player).getHandle()).playerConnection.sendPacket(packet);
    }
}

