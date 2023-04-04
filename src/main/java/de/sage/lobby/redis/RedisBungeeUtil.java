package de.sage.lobby.redis;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.sage.lobby.LobbySystem;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public record RedisBungeeUtil(RedisBungeeAPI redisBungeeAPI) {

    public RedisBungeeUtil(@NotNull RedisBungeeAPI redisBungeeAPI) {
        this.redisBungeeAPI = redisBungeeAPI;
    }

    @Override
    public @NotNull RedisBungeeAPI redisBungeeAPI() {
        return redisBungeeAPI;
    }

    public int getOnlinePlayers(String serverName) {
        return redisBungeeAPI.getPlayersOnServer(serverName).size();
    }

    public RegisteredServer getServerWithLeastPlayers(List<String> servers) {
        final class Tuple<A, B> {
            final A a;
            final B b;

            Tuple(A a, B b) {
                this.a = a;
                this.b = b;
            }
        }
        return LobbySystem.getInstance().getServer().getAllServers().stream()
                .filter(s -> servers.contains(s.getServerInfo().getName()))
                .map(server -> new Tuple<>(server, getOnlinePlayers(server.getServerInfo().getName())))
                .min(Comparator.comparing(t -> t.b))
                .map(tuple -> tuple.a)
                .orElseThrow(NoSuchElementException::new);
    }
}
