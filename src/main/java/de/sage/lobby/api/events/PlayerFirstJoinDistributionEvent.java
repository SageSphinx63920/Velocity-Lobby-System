package de.sage.lobby.api.events;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.annotation.AwaitingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@AwaitingEvent
public class PlayerFirstJoinDistributionEvent implements ResultedEvent<PlayerFirstJoinDistributionEvent.JoinResult>{

    private final Player player;
    private final RegisteredServer server;
    private JoinResult result;

    public PlayerFirstJoinDistributionEvent(Player player, @NotNull RegisteredServer server) {
        this.player = player;
        this.server = server;
        this.result = JoinResult.allowed();
    }

    public Player getPlayer() {
        return player;
    }

    public RegisteredServer getServer() {
        return server;
    }

    @Override
    public JoinResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(@NotNull JoinResult result) {
        this.result = result;
    }

    /**
     * Represents the result of the {@link PlayerFirstJoinDistributionEvent}.
     */
    public static final class JoinResult implements ResultedEvent.Result {

        private static final PlayerFirstJoinDistributionEvent.JoinResult ALLOWED = new PlayerFirstJoinDistributionEvent.JoinResult(true, null);
        private static final PlayerFirstJoinDistributionEvent.JoinResult DENIED = new PlayerFirstJoinDistributionEvent.JoinResult(false, null);

        private final boolean status;
        private final RegisteredServer server;

        private JoinResult(boolean status, RegisteredServer server) {
            this.status = status;
            this.server = server;
        }

        public Optional<RegisteredServer> getServer() {
            return Optional.ofNullable(server);
        }

        @Override
        public boolean isAllowed() {
            return status;
        }

        @Override
        public String toString() {
            return status ? "allowed" : "denied";
        }

        /**
         * Allows the user to join the server
         *
         * @return the allowed result
         */
        public static PlayerFirstJoinDistributionEvent.JoinResult allowed() {
            return ALLOWED;
        }

        /**
         * Prevents the user from joining the server.
         *
         * @return the denied result
         */
        public static PlayerFirstJoinDistributionEvent.JoinResult denied() {
            return DENIED;
        }

        /**
         * Allows the initial server to be changed
         *
         * @param server the server to send the player to
         * @return the allowed result
         */
        public static PlayerFirstJoinDistributionEvent.JoinResult server(@NotNull RegisteredServer server) {
            return new PlayerFirstJoinDistributionEvent.JoinResult(true, server);
        }

    }

}
