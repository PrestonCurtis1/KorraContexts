package me.unprankable.korraContexts;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import me.unprankable.korraContexts.hooks.LuckPermsHook;
import me.unprankable.korraContexts.managers.ContextsManager;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public final class KorraContexts extends JavaPlugin {
    public static KorraContexts plugin;
    public static Logger log;
    private static LuckPermsHook luckPermsHook;
    @Override
    public void onEnable() {
        plugin = this;
        KorraContexts.log = this.getLogger();
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("korracontexts")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("listcontexts")) {
                sender.sendMessage("§6=== KorraContexts Contexts ===");
                if (ContextsManager.contexts.isEmpty()) {
                    sender.sendMessage("§cNo contexts are currently registered.");
                } else {
                    sender.sendMessage("§bRegistered contexts (§f" + ContextsManager.contexts.size() + "§b):");
                    for (final ContextsManager.Context context : ContextsManager.contexts) {
                        sender.sendMessage("§7- §f" + context.getKey());
                    }
                }
                sender.sendMessage("§6==============================");
                return true;
            }

            sender.sendMessage("§6=== KorraContexts Info ===");
            sender.sendMessage("§bVersion: §f" + this.getDescription().getVersion());
            sender.sendMessage("§bDescription: §f" + this.getDescription().getDescription());
            sender.sendMessage("§bAuthors: §f" + String.join(", ", this.getDescription().getAuthors()));
            sender.sendMessage("§bWebsite: §f" + this.getDescription().getWebsite());
            sender.sendMessage("§6==========================");
            return true;
        }
        return false;
    }
}
