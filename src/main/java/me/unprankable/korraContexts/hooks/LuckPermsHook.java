package me.unprankable.korraContexts.hooks;

import me.unprankable.korraContexts.KorraContexts;
import me.unprankable.korraContexts.managers.ContextsManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class LuckPermsHook {
    private static LuckPerms luckPermsAPI;
    // Prevent nested LuckPerms context calculations from recursing into themselves.
    private static final ThreadLocal<Integer> CONTEXT_DEPTH = ThreadLocal.withInitial(() -> 0);

    public static boolean isCalculatingContexts() {
        return CONTEXT_DEPTH.get() > 0;
    }

    private static void enterCalculation() {
        CONTEXT_DEPTH.set(CONTEXT_DEPTH.get() + 1);
    }

    private static void exitCalculation() {
        final int nextDepth = CONTEXT_DEPTH.get() - 1;
        if (nextDepth <= 0) {
            CONTEXT_DEPTH.remove();
            return;
        }
        CONTEXT_DEPTH.set(nextDepth);
    }

    public boolean register() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null){
            luckPermsAPI = provider.getProvider();
            ContextsManager contextsManager = new ContextsManager();
            contextsManager.registerContexts();

            ContextCalculator<Player> bendingCalculator = new ContextCalculator<Player>() {
                @Override
                public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
                    enterCalculation();
                    try {
                        final int depth = CONTEXT_DEPTH.get();
                        for (ContextsManager.Context context : ContextsManager.contexts) {
                            // Prevent recursive Towny/ProjectKorra permission lookups from re-entering this one context.
                            if (depth > 1 && "KorraContexts:isregionprotected".equalsIgnoreCase(context.getKey())) {
                                continue;
                            }

                            try {
                                for (String value : context.calculator(target)) {
                                    consumer.accept(context.getKey(), value.toLowerCase());
                                }
                            } catch (StackOverflowError error) {
                                KorraContexts.log.warning("Skipping context '" + context.getKey() + "' due to recursive calculation.");
                            } catch (Exception exception) {
                                KorraContexts.log.warning("Failed to calculate context '" + context.getKey() + "': " + exception.getMessage());
                            }
                        }
                    } finally {
                        exitCalculation();
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
