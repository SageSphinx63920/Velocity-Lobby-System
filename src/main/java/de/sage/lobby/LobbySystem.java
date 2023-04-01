package de.sage.lobby;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.sage.lobby.command.CommandManager;
import de.sage.lobby.config.VelocityConfig;
import de.sage.lobby.event.DistributeEvents;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Plugin(id = "lobby-system", name = "Lobby System", version = "1.0.0",
        url = "https://sageee.xyz", description = "Distribute your players on different lobby servers",
        authors = {"SageSphinx63920"})
public class LobbySystem {

    private final Path dataDirectory;
    private final @Getter ProxyServer server;
    private final @Getter Logger logger;
    private static LobbySystem instance;
    private @Getter VelocityConfig config;

    @Inject
    public LobbySystem(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.config = new VelocityConfig(dataDirectory, "config.yml");

        //Register events
        this.server.getEventManager().register(this, new DistributeEvents());

        //Register Commands
        CommandManager commandManager = new CommandManager(this, config);
        commandManager.registerCommands();
    }

    public static LobbySystem getInstance() {
        return instance;
    }

    /**
     * Gets a server from the given order type
     *
     * @param servers List of server names to choose from
     * @param type    Order type to get the server
     * @return A server selected with the given type from the given list
     */
    @Nullable
    public RegisteredServer getServer(@NotNull List<String> servers, @NotNull SelectType type) {
        AtomicReference<RegisteredServer> selectedServer = new AtomicReference<>();

        switch (type) {
            case RANDOM -> {
                String randomServerName = servers.get(new Random().nextInt(servers.size()) - 1);

                LobbySystem.getInstance().getServer().getServer(randomServerName).ifPresentOrElse(selectedServer::set, () ->
                        LobbySystem.getInstance().getLogger().error("Server " + randomServerName + " was not found! Can not use it as random server on join!", new Throwable("Server not found!"))
                );
            }

            case PLAYERCOUNT -> {
                HashMap<Integer, List<RegisteredServer>> playercountMap = new HashMap<>();

                for (String serverName : servers) {
                    LobbySystem.getInstance().getServer().getServer(serverName).ifPresentOrElse(server -> {
                                if (playercountMap.containsKey(server.getPlayersConnected().size())) {
                                    List<RegisteredServer> newServerList = playercountMap.get(server.getPlayersConnected().size());
                                    newServerList.add(server);

                                    playercountMap.put(server.getPlayersConnected().size(), newServerList);
                                } else {
                                    playercountMap.put(server.getPlayersConnected().size(), List.of(server));
                                }
                            }, () ->
                                    LobbySystem.getInstance().getLogger().error("Server " + serverName + " was not found! Can not use it as lowest player count server on join!", new Throwable("Server not found!"))
                    );
                }

                List<RegisteredServer> lowestCountList = playercountMap.get(Collections.min(playercountMap.keySet()));
                RegisteredServer lowestCountServer = lowestCountList.get(new Random().nextInt(lowestCountList.size()) - 1);

                selectedServer.set(lowestCountServer);
            }
        }

        return selectedServer.get();
    }

    /**
     * Order type to select the server
     */
    public enum SelectType {
        RANDOM, PLAYERCOUNT
    }
}
