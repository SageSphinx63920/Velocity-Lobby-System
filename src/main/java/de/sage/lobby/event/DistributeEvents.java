package de.sage.lobby.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import de.sage.lobby.LobbySystem;
import de.sage.lobby.config.VelocityConfig;

import java.util.*;
import java.util.List;

public class DistributeEvents {

    private final String FIRST_JOIN_LOCATION = "distributing.first-join.";
    private final String SERVER_CHANGE_LOCATION = "distributing.on-server-change.";
    private VelocityConfig config;

    @Subscribe
    public void onPlayerChat(PlayerChooseInitialServerEvent event) {
        this.config = LobbySystem.getInstance().getConfig();

        if (config.getOption(false, FIRST_JOIN_LOCATION + "enabled")) {

            List<String> serverNames = config.getOption(Collections.emptyList(), "groups." + config.getOption("", FIRST_JOIN_LOCATION + "server-group"));
            if (serverNames.isEmpty()) return;

            switch (config.getOption("random", FIRST_JOIN_LOCATION + "order")) {
                case "random" ->
                        event.setInitialServer(LobbySystem.getInstance().getServer(serverNames, LobbySystem.SelectType.RANDOM));

                case "playercount" ->
                        event.setInitialServer(LobbySystem.getInstance().getServer(serverNames, LobbySystem.SelectType.PLAYERCOUNT));
            }
        }
    }

    @Subscribe
    public void onChange(ServerPreConnectEvent event) {
        if (config.getOption(false, SERVER_CHANGE_LOCATION + "enabled")) {
            if (event.getOriginalServer().getServerInfo().getName().matches(config.getOption("", SERVER_CHANGE_LOCATION + "listen-regex"))) {
                List<String> serverNames = config.getOption(Collections.emptyList(), "groups." + config.getOption("", SERVER_CHANGE_LOCATION + "server-group"));
                if (serverNames.isEmpty()) return;

                switch (config.getOption("random", SERVER_CHANGE_LOCATION + "order")) {
                    case "random" -> {
                        event.setResult(ServerPreConnectEvent.ServerResult.denied());
                        event.getPlayer().createConnectionRequest(LobbySystem.getInstance().getServer(serverNames, LobbySystem.SelectType.RANDOM)).fireAndForget();
                    }

                    case "playercount" -> {
                        event.setResult(ServerPreConnectEvent.ServerResult.denied());
                        event.getPlayer().createConnectionRequest(LobbySystem.getInstance().getServer(serverNames, LobbySystem.SelectType.PLAYERCOUNT)).fireAndForget();
                    }
                }
            }
        }
    }
}
