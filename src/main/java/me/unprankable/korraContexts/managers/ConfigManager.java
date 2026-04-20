package me.unprankable.korraContexts.managers;

import me.unprankable.korraContexts.KorraContexts;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ConfigManager {
    private final KorraContexts plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(final KorraContexts plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", false);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Bundled config.yml was not found. Creating a minimal config file.");
                try {
                    configFile.createNewFile();
                } catch (IOException ioException) {
                    plugin.getLogger().severe("Failed to create config.yml: " + ioException.getMessage());
                }
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        config.addDefault("debug", false);
        config.addDefault("prefix", "korracontexts:");
        config.addDefault("disabled-contexts", List.of());
        config.options().copyDefaults(true);

        try {
            config.save(configFile);
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save config.yml defaults: " + exception.getMessage());
        }

        KorraContexts.debug("Config loaded");
    }
    public void save() {
        try {
            config.save(configFile);
            KorraContexts.debug("Config saved");
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to save config.yml: " + exception.getMessage());
        }
    }
    public String getPrefix() {
        String prefix = config.getString("prefix", "korracontexts:");
        if (!prefix.endsWith(":")) {
            prefix += ":";
        }
        return prefix;
    }
    public FileConfiguration getConfig() {
        return config;
    }
}
