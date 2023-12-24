package xyz.nucleoid.extras.lobby.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicates;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.poi.PointOfInterestStorage;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.NEPointOfInterestTypes;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.lobby.tree.Ornament;
import xyz.nucleoid.extras.lobby.tree.OrnamentModel;
import xyz.nucleoid.extras.lobby.tree.TreeDecoration;

public class TreeDecorationBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogManager.getLogger(TreeDecorationBlockEntity.class);

    private static final String DECORATION_KEY = "decoration";

    private static final int SEARCH_RADIUS = 32;

    private TreeDecoration data = TreeDecoration.createEmpty();
    private final Map<Ornament, OrnamentModel> ornamentsToModels = new HashMap<>();

    private final ElementHolder holder = new ElementHolder();
    private HolderAttachment attachment;

    public TreeDecorationBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.TREE_DECORATION_ENTITY, pos, state);
    }

    private void setData(TreeDecoration data) {
        this.data = data;

        // Add models that have been added to the data
        for (var ornament : data.getOrnaments()) {
            if (!this.ornamentsToModels.containsKey(ornament)) {
                var model = new OrnamentModel(this, ornament);

                model.addToHolder(this.holder);
                this.ornamentsToModels.put(ornament, model);
            }
        }

        // Remove models that are no longer in the data
        this.ornamentsToModels.entrySet().removeIf(entry -> {
            if (!data.getOrnaments().contains(entry.getKey())) {
                entry.getValue().removeFromHolder(this.holder);
                return true;
            }

            return false;
        });
    }

    private void addOrnament(Ornament ornament) {
        this.setData(this.data.withOrnament(ornament));
        this.markDirty();
    }

    public void removeOrnament(Ornament ornament) {
        this.setData(this.data.exceptOrnament(ornament));
        this.markDirty();

        var pos = this.pos.toCenterPos().add(ornament.offset());

        float pitch = 1.3f + world.getRandom().nextFloat() * 0.2f;
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 0.5f, pitch);
    }

    public boolean placeOrnament(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockHitResult hitResult) {
        var item = TaterBoxItem.getPrimaryCollectedTater(player).asItem();
        if (item == Items.AIR) return false;

        var blockPos = hitResult.getBlockPos();

        var state = world.getBlockState(blockPos);
        if (!state.isIn(this.data.getSupportedBlocks())) return false;

        var pos = hitResult.getPos();
        var side = hitResult.getSide();

        if (side == Direction.UP) {
            return false;
        } else if (side != Direction.DOWN) {
            var belowPos = blockPos.add(side.getOffsetX(), side.getOffsetY() - 1, side.getOffsetZ());

            if (world.getBlockState(belowPos).isFullCube(world, belowPos)) {
                double minY = blockPos.getY() + OrnamentModel.HEIGHT;
                pos = pos.withAxis(Direction.Axis.Y, Math.max(minY, pos.getY()));
            }
        }

        var offset = pos.subtract(this.pos.toCenterPos());

        float yaw = player.getYaw() - 180;
        float hookYaw = MathHelper.wrapDegrees(player.getRandom().nextFloat() * 360);

        this.addOrnament(new Ornament(item, offset, yaw, hookYaw, player.getUuid()));

        world.emitGameEvent(player, GameEvent.ENTITY_PLACE, pos);

        float pitch = 1.3f + player.getRandom().nextFloat() * 0.2f;
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.BLOCKS, 0.5f, pitch);

        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains(DECORATION_KEY, NbtElement.COMPOUND_TYPE)) {
            TreeDecoration.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound(DECORATION_KEY))
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(this::setData);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        TreeDecoration.CODEC.encodeStart(NbtOps.INSTANCE, this.data)
                .resultOrPartial(LOGGER::error)
                .ifPresent(element -> {
                    nbt.put(DECORATION_KEY, element);
                });
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        this.holder.destroy();
    }

    public static void tick(World world, BlockPos pos, BlockState state, TreeDecorationBlockEntity blockEntity) {
        for (var model : blockEntity.ornamentsToModels.values()) {
            model.tick();
        }

        if (blockEntity.attachment == null && world instanceof ServerWorld serverWorld) {
            blockEntity.attachment = BlockBoundAttachment.ofTicking(blockEntity.holder, serverWorld, pos);
        }
    }

    public static Optional<TreeDecorationBlockEntity> findNearestTreeDecoration(ServerWorld world, BlockPos pos) {
        return world.getPointOfInterestStorage()
                .getNearestPosition(poiType -> {
                    return poiType.matchesKey(NEPointOfInterestTypes.TREE_DECORATION);
                }, Predicates.alwaysTrue(), pos, SEARCH_RADIUS, PointOfInterestStorage.OccupationStatus.ANY)
                .flatMap(decorationPos -> {
                    return world.getBlockEntity(decorationPos, NEBlocks.TREE_DECORATION_ENTITY);
                });
    }
}
