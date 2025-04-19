package xyz.nucleoid.extras.lobby.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.nucleoid.extras.integrations.http.NucleoidHttpClient;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LeaderboardDisplayEntity extends DisplayEntity.TextDisplayEntity implements PolymerEntity {
    private static final Style PLACE_NUMBER = Style.EMPTY.withColor(Formatting.GRAY).withBold(true);
    private static final Style PLACE_PLAYER = Style.EMPTY.withColor(Formatting.WHITE);
    private static final Style PLACE_VALUE = Style.EMPTY.withColor(Formatting.BLUE);

    private static final int REGULAR_UPDATE_WAIT_TIME = 20 * 60;
    private static final int FORCED_UPDATE_WAIT_TIME = 20 * 10;
    private static final int CHANGE_DISPLAYED_TIME_TIME = 20 * 10;
    private List<Identifier> leaderboardIds = List.of(Identifier.of("nucleoid", "games_played"));
    private final List<Text> leaderboards = new ArrayList<>();
    private int updateTimer = -1;
    private int displayTimer = CHANGE_DISPLAYED_TIME_TIME;
    private int currentId = 0;

    public LeaderboardDisplayEntity(EntityType<LeaderboardDisplayEntity> entityType, World world) {
        super(entityType, world);
        this.leaderboards.add(Text.empty());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        var ids = nbt.getList("leaderboards").stream().map(NbtElement::asString)
            .filter(Optional::isPresent).map(Optional::get).map(Identifier::tryParse).filter(Objects::nonNull).toList();
        this.leaderboardIds = ids;
        this.updateTimer = FORCED_UPDATE_WAIT_TIME;
        this.leaderboards.clear();

        for (var id : ids) {
            this.leaderboards.add(Text.literal("Waiting for update... [" + id + "]"));
        }

        if (this.leaderboards.isEmpty()) {
            this.leaderboards.add(Text.literal("EMPTY!!!"));
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
                    var text = Text.empty();

                    for (var entry : data) {
                        text.append(Text.literal(entry.ranking() + ". ").setStyle(PLACE_NUMBER));
                        String name;

                        var profile = this.getServer().getUserCache().getByUuid(entry.playerUuid());
                        if (profile.isPresent()) {
                            name = profile.get().getName();
                        } else {
                            name = "[Unknown player]";
                        }

                        text.append(Text.literal(name).setStyle(PLACE_PLAYER));
                        text.append(Text.literal("(").append("" + entry.value()).append(")").setStyle(PLACE_VALUE));
                        text.append("\n");
                    }

                    return text;
                }).thenAcceptAsync(text -> this.leaderboards.set(ia, text), this.getServer()));
            }

            CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).handleAsync((a, b) -> {
                this.updateTimer = REGULAR_UPDATE_WAIT_TIME;
                return null;
            }, this.getServer());
        }

        if (this.displayTimer-- == 0) {
            this.displayTimer = CHANGE_DISPLAYED_TIME_TIME;
            this.currentId++;
            if (this.currentId >= this.leaderboards.size()) {
                this.currentId = 0;
            }

            this.dataTracker.set(DisplayTrackedData.Text.TEXT, this.leaderboards.get(this.currentId));
        }
    }



    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.leaderboardIds != null) {
            var list = new NbtList();
            this.leaderboardIds.stream().map(x -> NbtString.of(x.toString())).forEach(list::add);

            nbt.put("leaderboards", list);
        }
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.TEXT_DISPLAY;
    }
}
