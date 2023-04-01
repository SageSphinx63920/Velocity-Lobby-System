package de.sage.lobby.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.sage.lobby.LobbySystem;
import de.sage.lobby.config.VelocityConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class TemplateCommand implements SimpleCommand {

    private final String commandLocation;
    private final VelocityConfig config;

    /**
     * Template command of the plugin
     *
     * @param commandLocation Config location in the file
     * @param config          Config of the plugin
     */
    public TemplateCommand(@NotNull String commandLocation, @NotNull VelocityConfig config) {
        this.commandLocation = commandLocation;
        this.config = config;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            String orderType = config.getOption("random", commandLocation + "order");

            switch (orderType) {
                case "random" -> {
                    String serverGroup = config.getOption(config.getOption("", "distributing.commands.default-group"), commandLocation + "group");

                    List<String> serverNames = config.getOption(Collections.emptyList(), "groups." + serverGroup);
                    if (serverNames.isEmpty()) return;

                    player.createConnectionRequest(LobbySystem.getInstance().getServer(serverNames, LobbySystem.SelectType.RANDOM));
                }

                case "playercount" -> {
                    String serverGroup = config.getOption(config.getOption("", "distributing.commands.default-group"), commandLocation + "group");

                    List<String> serverNames = config.getOption(Collections.emptyList(), "groups." + serverGroup);
                    if (serverNames.isEmpty()) return;

                    player.createConnectionRequest(LobbySystem.getInstance().getServer(serverNames, LobbySystem.SelectType.PLAYERCOUNT));
                }
            }
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        String permission = config.getOption(config.getOption("command.hub", "distributing.commands.default-permission"), commandLocation + "permission");

        if (permission.equals("none")) {
            return true;
        } else
            return invocation.source().hasPermission(permission);
    }
}
