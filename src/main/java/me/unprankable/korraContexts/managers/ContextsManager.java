package me.unprankable.korraContexts.managers;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;

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
        new Context("KorraContexts:element", player -> {
            return BendingManager.getElements(player);
        }, Arrays.stream(Element.getAllElements()).map(Element::getName).collect(Collectors.toList()));
        new Context("KorraContexts:sub_element", player -> {
            return BendingManager.getSubElements(player);
        }, Arrays.stream(Element.getAllSubElements()).map(Element::getName).collect(Collectors.toList()));
        new Context("KorrrContexts:ability", player -> {
            return BendingManager.boundAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("KorraContexts:canBind", player -> {
            return BendingManager.bindableAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("KorraContexts:canBend", player -> {
            return BendingManager.bendableAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("KorraContexts:active", player -> {
            return BendingManager.activeAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));
        new Context("KorraContexts:active", player -> {
            return BendingManager.cooldownAbilities(player);
        }, CoreAbility.getAbilities().stream().map(CoreAbility::getName).collect(Collectors.toList()));

    }
    public class Context {
        private final List<String> possible;
        private String key;
        private Function<Player, List<String>> calculate;
        public Context(String key, Function<Player, List<String>> calculator, List<String> possible) {
            this.key = key;
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
            List<String> possible = new ArrayList<>();
            return possible;
        }
    }
}
