package de.sage.lobby.command;

import de.sage.lobby.LobbySystem;
import de.sage.lobby.config.VelocityConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CommandManager {

    private final LobbySystem plugin;
    private final VelocityConfig config;
    private final com.velocitypowered.api.command.CommandManager commandManager;

    /**
     * Object to manage and create commands of the plugin
     *
     * @param plugin Instance of the plugin
     * @param config Config of the plugin
     */
    public CommandManager(@NotNull LobbySystem plugin, @NotNull VelocityConfig config) {
        this.plugin = plugin;
        this.config = config;

        this.commandManager = plugin.getServer().getCommandManager();
    }

    /**
     * Registers all commands from the config
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerCommands() {
        ArrayList<LinkedHashMap> commands = config.getOption(new ArrayList<>(), "commands");

        commands.forEach(map -> map.keySet().forEach(key -> {
                TemplateCommand command = new TemplateCommand("commands." + key, config);

                commandManager.register((String) key, command);
            })
        );
    }
}
