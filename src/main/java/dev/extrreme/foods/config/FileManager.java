package dev.extrreme.foods.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class FileManager {

    protected final File file;
    protected final FileConfiguration configuration;

    public FileManager(JavaPlugin plugin, String fileName, boolean load) {
        this.file = new File(plugin.getDataFolder(), (fileName.contains(".yml") ? fileName : fileName + ".yml"));
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        if (load) {
            load();
        }
    }

    protected abstract void load();

    protected void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
