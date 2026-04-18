package me.unprankable.korraContexts.managers;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.region.RegionProtection;
import com.projectkorra.projectkorra.util.Cooldown;
import me.unprankable.korraContexts.hooks.LuckPermsHook;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class BendingManager {
    public static List<String> getElements(Player player) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return List.of("nonbender");
        final Set<Element> elements = new LinkedHashSet<>(bPlayer.getElements());
        final long now = System.currentTimeMillis();
        final Set<Element> tempElements = new LinkedHashSet<>();
        for (final Element element : bPlayer.getTempElements().keySet()) {
            final long expiration = bPlayer.getTempElementTime(element);
            if (expiration > now) {
                tempElements.add(element);
            }
        }
        elements.addAll(tempElements);
        if (elements.isEmpty() || elements == null) {
            return List.of("nonbender");
        }
        final Set<String> elementNames = new LinkedHashSet<>();
        for (final Element element : elements) {
            elementNames.add(element.getName());
        }
        if (hasAvatarElements(elements)) {
            elementNames.add("avatar");
        }
        if (elementNames.isEmpty()) {
            elementNames.add("nonbender");
        }
        return List.copyOf(elementNames);
    }

    public static List<String> getSubElements(Player player) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return List.of();
        final Set<Element> subElements = new LinkedHashSet<>(bPlayer.getSubElements());
        final long now = System.currentTimeMillis();
        final Set<Element> tempSubElements = new LinkedHashSet<>();
        for (final Element subElement : bPlayer.getTempSubElements().keySet()) {
            final long expiration = bPlayer.getTempSubElementTime((Element.SubElement) subElement);
            if (expiration > now) {
                tempSubElements.add(subElement);
            }
        }
        subElements.addAll(tempSubElements);
        if (subElements.isEmpty() || subElements == null) {
            return List.of();
        }
        final Set<String> subElementNames = new LinkedHashSet<>();
        for (final Element subElement : subElements) {
            subElementNames.add(subElement.getName());
        }
        return List.copyOf(subElementNames);
    }

    public static boolean hasAvatarElements(Set<Element> elements) {
        return elements.contains(Element.AIR)
                && elements.contains(Element.WATER)
                && elements.contains(Element.EARTH)
                && elements.contains(Element.FIRE);
    }

    public static List<String> boundAbilities(Player player) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        Set<String> abilities = new LinkedHashSet<>();
        if (bPlayer == null) return List.of();
        for (int slot = 1; slot <= 9; slot++) {
            String abilityName = bPlayer.getAbilities().get(slot);
            if (abilityName != null) {
                abilities.add(abilityName);
            }
        }
        return List.copyOf(abilities);
    }
    public static List<String> activeAbilities(Player player) {
        List<String> active = new ArrayList<>();
        for (CoreAbility ability : CoreAbility.getAbilitiesByInstances()) {
            Player abilityPlayer = ability.getPlayer();
            if (abilityPlayer != null && abilityPlayer.getUniqueId().equals(player.getUniqueId())){
                active.add(ability.getName());
            }
        }
        return active;
    }
    public static List<String> cooldownAbilities(Player player) {
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if (bPlayer == null) return List.of();
        Map<String, Cooldown> cooldownsMap = bPlayer.getCooldowns();
        List<String> cooldowns = cooldownsMap.keySet().stream()
                .filter(bPlayer::isOnCooldown)
                .collect(Collectors.toList());
        return cooldowns;
    }
    public static List<String> isRegionProtected(Player player) {

        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        final CoreAbility boundAbility = bPlayer != null ? bPlayer.getBoundAbility() : null;
        final boolean protectedState = RegionProtection.isRegionProtected(player, player.getLocation(), boundAbility);
        return List.of(String.valueOf(protectedState));
    }
}

