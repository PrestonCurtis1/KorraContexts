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

/*




contexts.add("KorraContexts:active");

contexts.add("KorraContexts:cooldown");*/
    public void registerContexts() {
        new Context("element", player -> {
            return BendingManager.getElements(player);
        }, Arrays.stream(Element.getAllElements()).map(Element::getName).collect(Collectors.toList()));
        new Context("sub_element", player -> {
            return BendingManager.getSubElements(player);
        }, Arrays.stream(Element.getAllSubElements()).map(Element::getName).collect(Collectors.toList()));
        new Context("ability", player -> {
            return BendingManager.boundAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("active", player -> {
            return BendingManager.activeAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("cooldown", player -> {
            return BendingManager.cooldownAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("isregionprotected", player -> {
            return BendingManager.isRegionProtected(player);
        }, List.of("true", "false"));
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
        private final List<String> possible;
        private String key;
        private Function<Player, List<String>> calculate;
        public Context(String key, Function<Player, List<String>> calculator, List<String> possible) {
            this.key = "KorraContexts:" + key;
            this.possible = possible;
            this.calculate = calculator;
            contexts.add(this);
        }
        public String getKey() {
            return key;
        }
        public List<String> calculator(Player player){
            List<String> values = this.calculate.apply(player);
            return values;
        }
        public List<String> possibleValues() {
            return possible;
        }
    }
}
