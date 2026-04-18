package me.unprankable.korraContexts;

import java.util.jar.JarFile;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;


public class ContextLoadEvent<T> extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Plugin plugin;
    private final T loadable;
    private final JarFile jarFile;

    /**
     * Creates a new AbilityLoadEvent.
     *
     * @param plugin The instance of ProjectKorra
     * @param loadable The class that was loaded
     * @param jarFile The JarFile the class was loaded from
     */
    public ContextLoadEvent(final Plugin plugin, final T loadable, final JarFile jarFile) {
        this.plugin = plugin;
        this.loadable = loadable;
        this.jarFile = jarFile;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public JarFile getJarFile() {
        return this.jarFile;
    }

    public T getLoadable() {
        return this.loadable;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }


}
