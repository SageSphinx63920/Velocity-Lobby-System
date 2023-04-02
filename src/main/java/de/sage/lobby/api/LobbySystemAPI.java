package de.sage.lobby.api;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.sage.lobby.LobbySystem;
import de.sage.lobby.config.VelocityConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The API of the Lobby System
 *
 * @author SageSphinx63920
 * @since 1.0.0
 */
public class LobbySystemAPI {

    /**
     * Get a server from a given list with an order type
     *
     * @param servers The list of servers
     * @param selectType The order type
     * @return The selected server
     */
    @Nullable
    public static RegisteredServer getServerByOder(@NotNull List<RegisteredServer> servers, @NotNull LobbySystem.SelectType selectType) {
        List<String> names = new ArrayList<>();
        servers.forEach(server -> names.add(server.getServerInfo().getName()));

        return LobbySystem.getInstance().getServer(names, selectType);
    }

    /**
     * Gets a list of servers in a given group
     *
     * @param group The name of the group in the config
     * @return A list of existing servers in the given group. Can be empty
     */
    @NotNull
    public static List<RegisteredServer> getServersInGroup(@NotNull String group) {
        VelocityConfig config = LobbySystem.getInstance().getConfig();

        List<String> serverNames = config.getOption(Collections.emptyList(), "groups." + group);
        List<RegisteredServer> servers = new ArrayList<>();

        serverNames.forEach(serverName -> {
            LobbySystem.getInstance().getServer().getServer(serverName).ifPresentOrElse(servers::add, () -> {
                throw new IllegalStateException("Server " + serverName + " not found");
            });
        });

        return servers;
    }

    /**
     * Checks if a server is in a given group
     *
     * @param server The server to check
     * @param group The group to check
     * @return True if the server is in the group, false if not
     */
    public static boolean isServerInGroup(@NotNull RegisteredServer server, @NotNull String group) {
        return getServersInGroup(group).stream().anyMatch(servers -> servers.equals(server));
    }

    /**
     * Gets a server in a given group with an order type
     *
     * @param group The group to get the server from
     * @param selectType The order type to get the server
     * @return The selected server
     */
    @Nullable
    public static RegisteredServer getServerInGroup(@NotNull String group, @NotNull LobbySystem.SelectType selectType) {
        return getServerByOder(getServersInGroup(group), selectType);
    }

    /**
     * Checks if the first join distribution is active
     *
     * @return True if active, false if not
     */
    public static boolean isFirstJoinActive() {
        return LobbySystem.getInstance().getConfig().getOption(false, "distributing.first-join.enabled");
    }

    /**
     * Checks if the distribution on server change is active
     *
     * @return True if active, false if not
     */
    public static boolean isServerChangeActive() {
        return LobbySystem.getInstance().getConfig().getOption(false, "distributing.on-server-change.enabled");
    }

    /**
     * Checks if the commands are active
     *
     * @return True if active, false if not
     */
    public static boolean areCommandsActive() {
        return LobbySystem.getInstance().getConfig().getOption(false, "distributing.commands.enabled");
    }

}
