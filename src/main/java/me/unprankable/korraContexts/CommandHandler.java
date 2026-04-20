package me.unprankable.korraContexts;

import me.unprankable.korraContexts.managers.ContextsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final KorraContexts plugin;
    private static final List<String> SUBCOMMANDS = List.of("listcontexts", "context", "reload");

    public CommandHandler(final KorraContexts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (!command.getName().equalsIgnoreCase("korracontexts")) {
            KorraContexts.debug("Received command '" + command.getName() + "' but expected 'korracontexts'. Ignoring.");
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("listcontexts")) {
            sender.sendMessage("§6=== KorraContexts Contexts ===");
            if (ContextsManager.contexts.isEmpty()) {
                sender.sendMessage("§cNo contexts are currently registered.");
            } else {
                sender.sendMessage("§bRegistered contexts (§f" + ContextsManager.contexts.size() + "§b):");
                for (final ContextsManager.Context context : ContextsManager.contexts) {
                    sender.sendMessage("§7- §f" + context.getKey() + " §7(" + context.getAuthor() + ")");
                }
            }
            sender.sendMessage("§6==============================");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("context")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /" + label + " context <name>");
                return true;
            }

            final ContextsManager.Context context = findContext(args[1]);
            if (context == null) {
                sender.sendMessage("§cUnknown context: §f" + args[1]);
                return true;
            }

            sender.sendMessage("§6=== Context Info ===");
            sender.sendMessage("§bKey: §f" + context.getKey());
            sender.sendMessage("§bAuthor: §f" + context.getAuthor());
            sender.sendMessage("§bDescription: §f" + context.getDescription());
            sender.sendMessage("§6====================");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("KorraContexts.admin")) {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }

            KorraContexts.getConfigManager().reload();
            final ContextsManager contextsManager = new ContextsManager();
            contextsManager.reloadContexts();

            sender.sendMessage("§aKorraContexts reloaded.");
            sender.sendMessage("§7Loaded contexts: §f" + ContextsManager.contexts.size());
            return true;
        }

        sender.sendMessage("§6=== KorraContexts Info ===");
        sender.sendMessage("§bVersion: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§bDescription: §f" + plugin.getDescription().getDescription());
        sender.sendMessage("§bAuthors: §f" + String.join(", ", plugin.getDescription().getAuthors()));
        sender.sendMessage("§bWebsite: §f" + plugin.getDescription().getWebsite());
        sender.sendMessage("§6==========================");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        if (!command.getName().equalsIgnoreCase("korracontexts")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, completions);
            completions.sort(String.CASE_INSENSITIVE_ORDER);
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("context")) {
            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], getContextNames(), completions);
            completions.sort(String.CASE_INSENSITIVE_ORDER);
            return completions;
        }

        return Collections.emptyList();
    }

    private ContextsManager.Context findContext(final String input) {
        final String normalizedInput = input.toLowerCase(Locale.ROOT);
        for (final ContextsManager.Context context : ContextsManager.contexts) {
            final String fullKey = context.getKey();
            final String shortKey = extractShortKey(fullKey);
            if (fullKey.equalsIgnoreCase(input) || shortKey.equals(normalizedInput)) {
                return context;
            }
        }
        return null;
    }

    private List<String> getContextNames() {
        final Set<String> names = new LinkedHashSet<>();
        for (final ContextsManager.Context context : ContextsManager.contexts) {
            names.add(extractShortKey(context.getKey()));
        }
        return new ArrayList<>(names);
    }

    private String extractShortKey(final String fullKey) {
        final int separator = fullKey.indexOf(':');
        if (separator == -1 || separator + 1 >= fullKey.length()) {
            return fullKey.toLowerCase(Locale.ROOT);
        }
        return fullKey.substring(separator + 1).toLowerCase(Locale.ROOT);
    }
}

