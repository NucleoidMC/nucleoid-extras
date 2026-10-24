package xyz.nucleoid.extras.lobby.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.data.DisplayEntityData;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import xyz.nucleoid.extras.integrations.http.NucleoidHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeaderboardDisplayEntity extends Display.TextDisplay implements PolymerEntity {
    private static final Style PLACE_NUMBER = Style.EMPTY.withColor(ChatFormatting.YELLOW).withBold(true);
    private static final Style PLACE_PLAYER = Style.EMPTY.withColor(ChatFormatting.WHITE);
    private static final Style PLACE_VALUE = Style.EMPTY.withColor(ChatFormatting.GOLD);

    private static final int REGULAR_UPDATE_WAIT_TIME = 20 * 60;
    private static final int FORCED_UPDATE_WAIT_TIME = 20 * 10;
    private static final int CHANGE_DISPLAYED_TIME_TIME = 20 * 10;
    private List<Identifier> leaderboardIds = List.of(Identifier.fromNamespaceAndPath("nucleoid", "games_played"));
    private final List<Component> leaderboards = new ArrayList<>();
    private int updateTimer = -1;
    private int displayTimer = CHANGE_DISPLAYED_TIME_TIME;
    private int currentId = 0;

    public LeaderboardDisplayEntity(EntityType<LeaderboardDisplayEntity> entityType, Level world) {
        super(entityType, world);
        this.leaderboards.add(Component.empty());
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);

        var ids = nbt.listOrEmpty("leaderboards", Identifier.CODEC).stream().toList();
        this.leaderboardIds = ids;
        this.updateTimer = FORCED_UPDATE_WAIT_TIME;
        this.leaderboards.clear();

        for (var id : ids) {
            this.leaderboards.add(Component.literal("Waiting for update... [" + id + "]"));
        }

        if (this.leaderboards.isEmpty()) {
            this.leaderboards.add(Component.literal("EMPTY!!!"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.updateTimer-- == 0) {
            int i = 0;
            var list = new ArrayList<CompletableFuture<?>>(this.leaderboardIds.size());
            for (var id : this.leaderboardIds) {
                var ia = i++;
                list.add(NucleoidHttpClient.getLeaderboard(id).thenApplyAsync(data -> {
                    var text = Component.empty();

                    for (int x = 0; x < data.size(); x++) {
                        var entry = data.get(x);

                        text.append(Component.literal(entry.ranking() + ". ").setStyle(PLACE_NUMBER));
                        String name;

                        var profile = this.level().getServer().services().nameToIdCache().get(entry.playerUuid());
                        if (profile.isPresent()) {
                            name = profile.get().name();
                        } else {
                            name = "[Unknown player]";

                            CompletableFuture.supplyAsync(() -> this.level().getServer().services().profileResolver().fetchById(entry.playerUuid())).thenAccept( opt -> {
                                opt.ifPresent(p -> this.level().getServer().services().nameToIdCache().add(new NameAndId(p)));
                            });
                        }

                        text.append(Component.literal(name).setStyle(PLACE_PLAYER)).append(" ");
                        text.append(Component.literal("(").append("" + entry.value()).append(")").setStyle(PLACE_VALUE));
                        if (x != data.size() - 1) {
                            text.append("\n");
                        }
                    }

                    return text;
                }).thenAcceptAsync(text -> this.leaderboards.set(ia, text), this.level().getServer()));
            }

            CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).handleAsync((a, b) -> {
                this.updateTimer = REGULAR_UPDATE_WAIT_TIME / 10;
                return null;
            }, this.level().getServer());
        }

        if (this.displayTimer-- == 0) {
            this.displayTimer = CHANGE_DISPLAYED_TIME_TIME;
            this.currentId++;
            if (this.currentId >= this.leaderboards.size()) {
                this.currentId = 0;
            }

            this.entityData.set(DisplayEntityData.Text.TEXT, this.leaderboards.get(this.currentId));
        }
    }



    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.leaderboardIds != null) {
            var list = nbt.list("leaderboards", Identifier.CODEC);
            this.leaderboardIds.forEach(list::add);
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityTypes.TEXT_DISPLAY;
    }
}
