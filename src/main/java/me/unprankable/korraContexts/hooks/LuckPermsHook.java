package me.unprankable.korraContexts.hooks;

import me.unprankable.korraContexts.managers.ContextsManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

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

            ContextCalculator<Player> bendingCalculator = new ContextCalculator<Player>() {
                @Override
                public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
                    for (ContextsManager.Context context : ContextsManager.contexts) {
                        for (String value : context.calculator(target)) {
                            consumer.accept(context.getKey(), value.toLowerCase());
                        }
                    }
                }

                @Override
                public @NotNull ContextSet estimatePotentialContexts() {
                    ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
                    for (ContextsManager.Context context : ContextsManager.contexts) {
                        for (String potentialValue : context.possibleValues()) {
                            if (potentialValue != null) {
                                builder.add(context.getKey(), potentialValue.toLowerCase());
                            }
                        }
                    }
                    return builder.build();
                }
            };

            luckPermsAPI.getContextManager().registerCalculator(bendingCalculator);
            return true;
        } else {
            return false;
        }
    }


}
