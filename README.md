# KorraContexts

KorraContexts is a ProjectKorra side plugin that registers dynamic LuckPerms contexts based on a player's bending state.

It lets you target permissions by things like element, sub-element, bound/active ability, cooldowns, and region protection.

## Features

- Registers LuckPerms contexts from ProjectKorra player data
- Includes built-in contexts for element, ability, cooldown, and more
- Supports loading additional context providers from JARs in a `/contexts` folder
- Includes a command to list all registered contexts in-game

## Requirements

- Java 21+
- Spigot/Paper API `1.21.x`
- ProjectKorra `1.12.0`
- LuckPerms `5.4`

> `ProjectKorra` and `LuckPerms` are hard dependencies. The plugin disables itself if either is missing.

## Built-in Context Keys

All context keys are namespaced as `KorraContexts:<key>`.

- `KorraContexts:element`
- `KorraContexts:sub_element`
- `KorraContexts:ability` (bound hotbar abilities)
- `KorraContexts:active` (currently active ability instances)
- `KorraContexts:cooldown` (abilities currently on cooldown)
- `KorraContexts:isregionprotected` (`true`/`false`)

### Notable Values

- `KorraContexts:element` may include `nonbender`.
- `KorraContexts:element` also adds `avatar` when all four base elements are present.
- Context values are emitted in lowercase to LuckPerms.

## Command

- `/korracontexts listcontexts`
- Alias: `/kc`
- Permission: `KorraContexts.admin` (default: op)

Running `listcontexts` prints all currently registered context keys.

## Example LuckPerms Usage

```text
/lp group firebender permission set my.plugin.fire true context korracontexts:element=fire
/lp user SomePlayer permission set my.plugin.combo true context korracontexts:active=lightning
/lp group protected permission set my.plugin.safe true context korracontexts:isregionprotected=true
```

LuckPerms context matching is case-insensitive in practice, but this plugin sends lowercase values.

## Addon Contexts

On startup, KorraContexts scans this folder for addon JARs:

- `<server>/plugins/KorraContexts/contexts`

### Addon Context Checklist

- Create a separate JAR (not a Bukkit plugin JAR)
- Put your compiled addon JAR in `plugins/KorraContexts/contexts`
- Add at least one concrete class that extends `ContextsManager.Context`
- Your context class must have a **no-argument constructor**
- Use a key without the namespace prefix (KorraContexts adds `KorraContexts:` automatically)

### Create an Addon Context

`ContextsManager.Context` takes:

1. `key` (string)
2. `calculator` (`Function<Player, List<String>>`)
3. `possibleValues` (`List<String>`)

Minimal example:

```java
package your.package;

import me.unprankable.korraContexts.managers.ContextsManager;
import org.bukkit.entity.Player;

import java.util.List;

public final class SneakingContext extends ContextsManager.Context {
    public SneakingContext() {
        super(
                "issneaking",
                player -> List.of(String.valueOf(player.isSneaking())),
                List.of("true", "false")
        );
    }
}
```

This registers the context key as `KorraContexts:issneaking`.

Repository and Dependency for `pom.xml`:

```xml
<dependencies>
    <repositories>
        <repository>
            <id>github-korracontexts</id>
            <url>https://maven.pkg.github.com/PrestonCurtis1/KorraContexts</url>
        </repository>
    </repositories>
    <dependency>    
        <groupId>me.unprankable</groupId>
        <artifactId>korracontexts</artifactId>
        <version>1.0.2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Loader Rules (Important)

- JARs containing `plugin.yml` are treated as plugins and are ignored in the contexts folder
- Classes must be non-abstract and assignable to `ContextsManager.Context`
- Classes without a no-arg constructor fail to load

## Building

```bash
mvn clean package
```

The project uses `maven-shade-plugin` during `package`.

## Project Metadata

- Group: `me.unprankable`
- Artifact: `korracontexts`
- Version (pom): `1.0.2`
- Website: `https://github.com/PrestonCurtis1/KorraContexts`