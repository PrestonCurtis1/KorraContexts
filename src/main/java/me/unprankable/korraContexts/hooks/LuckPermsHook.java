package me.unprankable.korraContexts.hooks;

import me.unprankable.korraContexts.managers.ContextsManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class LuckPermsHook {
    private static LuckPerms luckPermsAPI;
    public boolean register() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null){
            luckPermsAPI = provider.getProvider();
            ContextsManager contextsManager = new ContextsManager();
            contextsManager.registerContexts();

            for (ContextsManager.Context context : ContextsManager.contexts) {
                luckPermsAPI.getContextManager().registerCalculator((target, consumer) -> {
                    if (target instanceof Player){
                        Player player = (Player) target;
                        for (String value : context.calculator(player)){
                            consumer.accept(context.getKey(), value);
                        }
                    }
                });
            }
            return true;
        } else {
            return false;
        }
    }

}
