package xyz.nucleoid.extras.lobby;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;

import java.util.ArrayList;
import java.util.List;

public class PlayerLobbyState {

    public static final PlayerDataStorage<PlayerLobbyState> STORAGE = new JsonDataStorage<>("nucleoid_extras", PlayerLobbyState.class);
    public final List<TinyPotatoBlock> collectedTaters = new ArrayList<>();

    public static PlayerLobbyState get(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return new PlayerLobbyState();
        }

        var data = PlayerDataApi.getCustomDataFor(serverPlayer, STORAGE);
        if (data == null) {
            data = new PlayerLobbyState();
            PlayerDataApi.setCustomDataFor(serverPlayer, STORAGE, data);
        }

        return data;
    }
}
