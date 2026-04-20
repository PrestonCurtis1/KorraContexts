package me.unprankable.korraContexts.managers;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.unprankable.korraContexts.AddonContextLoader;
import me.unprankable.korraContexts.KorraContexts;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContextsManager {
    public static List<Context> contexts = new ArrayList<>();
    public static List<String> disabledContexts = KorraContexts.getConfigManager().getConfig().getStringList("disabled-contexts");
    public void registerContexts() {
        new Context("element", player -> {
            return BendingManager.getElements(player);
        }, Arrays.stream(Element.getAllElements()).map(Element::getName).collect(Collectors.toList()), "Unprankable").setDescription("Returns the elements a player has. If the player has no elements, it will return 'nonbender'. If the player has the Avatar element, it will also return 'avatar'.");
        new Context("sub_element", player -> {
            return BendingManager.getSubElements(player);
        }, Arrays.stream(Element.getAllSubElements()).map(Element::getName).collect(Collectors.toList()), "Unprankable").setDescription("Returns the sub-elements a player has. If the player has no sub-elements, it will return an empty list.");
        new Context("ability", player -> {
            return BendingManager.boundAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()), "Unprankable").setDescription("Returns the abilities a player has currently bound. If the player has no abilities bound, it will return an empty list.");
        new Context("active", player -> {
            return BendingManager.activeAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()), "Unprankable").setDescription("Returns the abilities a player currently has active. If the player has no abilities active, it will return an empty list.");
        new Context("cooldown", player -> {
            return BendingManager.cooldownAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()), "Unprankable").setDescription("Returns the abilities a player currently has on cooldown. If the player has no abilities on cooldown, it will return an empty list.");
        new Context("isregionprotected", player -> {
            return BendingManager.isRegionProtected(player);
        }, List.of("true", "false"), "Unprankable").setDescription("Returns whether the player is currently in a region protected by ProjectKorra's region system. If the player is in a protected region, it will return 'true', otherwise it will return 'false'.");
        registerAddonContexts("/contexts");
    }

    public static void registerAddonContexts(final String folder){
        final KorraContexts plugin = KorraContexts.plugin;
        final File path = new File(plugin.getDataFolder().toString() + folder);
        if (!path.exists()){
            path.mkdir();
        }
        final AddonContextLoader<Context> contextLoader = new AddonContextLoader<Context>(plugin, path);
        final List<Context> loadedContexts = contextLoader.load(Context.class, Context.class);

        for (final Context context : loadedContexts) {
            if (context == null) {
                KorraContexts.log.warning("Failed to load context from " + context.getClass().getName() + " as it is null");
                continue;
            }
            final Context loadedContext = (Context) context;
            final String key = loadedContext.getKey();

            if (key == null || key.equals("KorraContexts:")) {
                KorraContexts.log.warning("Failed to load context from " + context.getClass().getName() + " as it does not have a valid key");
                continue;
            }

            KorraContexts.log.info("Loaded addon context: " + key + " (" + context.getClass().getName() + ")");
        }
    }


    public static class Context {
        private List<String> possible;
        private String key;
        private Function<Player, List<String>> calculate;
        private String author;
        public Context(String key, Function<Player, List<String>> calculator, List<String> possible, String author) {
            if (disabledContexts.contains(key.toLowerCase())) {
                KorraContexts.debug("Context '" + key + "' is disabled in the config and will not be registered.");
                return;
            }
            this.key = KorraContexts.getConfigManager().getPrefix() + key.toLowerCase();
            this.possible = possible;
            this.calculate = calculator;
            this.author = author;
            contexts.add(this);
            KorraContexts.debug("Registered context: " + this.key);
        }
        public Context(String key, Function<Player, List<String>> calculator, List<String> possible) {
            this(key, calculator, possible, "Unknown");
        }
        public String getKey() {
            return key;
        }
        public List<String> calculator(Player player){
            List<String> values = this.calculate.apply(player);
            return values;
        }
        public String getShortKey() {
            return key.replace(KorraContexts.getConfigManager().getPrefix(), "");
        }
        public String getDescription() {
            return KorraContexts.getConfigManager().getConfig().getString("descriptions." + getShortKey().toLowerCase(), "No description provided.");
        }
        public String setDescription(String description, boolean overwrite) {
            if (!overwrite && !getDescription().equals("No description provided.")) {
                KorraContexts.debug("Context '" + getKey() + "' already has a description and overwrite is false, so the description will not be updated.");
                return getDescription();
            }
            KorraContexts.getConfigManager().getConfig().set("descriptions." + getShortKey().toLowerCase(), description);
            try {
                KorraContexts.getConfigManager().save();
            } catch (Exception e) {
                KorraContexts.log.warning("Failed to save context description for '" + getKey() + "': " + e.getMessage());
            }
            return description;
        }
        public String setDescription(String description) {
            return setDescription(description, false);
        }
        public String getAuthor() {
            return author;
        }
        public List<String> possibleValues() {
            return possible;
        }
    }
}
