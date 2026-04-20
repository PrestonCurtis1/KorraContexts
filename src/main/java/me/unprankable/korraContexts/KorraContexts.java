package me.unprankable.korraContexts;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import me.unprankable.korraContexts.hooks.LuckPermsHook;
import me.unprankable.korraContexts.managers.ConfigManager;

import java.util.logging.Logger;

public final class KorraContexts extends JavaPlugin {
    public static KorraContexts plugin;
    public static Logger log;
    private static ConfigManager configManager;
    private static LuckPermsHook luckPermsHook;
    @Override
    public void onEnable() {
        plugin = this;
        KorraContexts.log = this.getLogger();
        configManager = new ConfigManager(this);
        configManager.load();
        if (getCommand("korracontexts") != null) {
            final CommandHandler commandHandler = new CommandHandler(this);
            getCommand("korracontexts").setExecutor(commandHandler);
            getCommand("korracontexts").setTabCompleter(commandHandler);
        }
        KorraContexts.log.info("KorraContexts has been enabled!");
        if(Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            KorraContexts.log.info("LuckPerms detected, hooking into it...");
            luckPermsHook = new LuckPermsHook();
            luckPermsHook.register();
        } else {
            KorraContexts.log.warning("LuckPerms not detected, disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);

        }
        if(Bukkit.getPluginManager().isPluginEnabled("ProjectKorra")) {
            KorraContexts.log.info("ProjectKorra detected, hooking into it...");

        } else {
            KorraContexts.log.warning("ProjectKorra not detected, disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    public static void debug(String message) {
        if (configManager != null && configManager.getConfig().getBoolean("debug")) {
            log.info("[DEBUG] " + message);
        }
    }
    public static ConfigManager getConfigManager() {
        return configManager;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
