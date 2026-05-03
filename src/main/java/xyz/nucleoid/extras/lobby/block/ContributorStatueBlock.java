package xyz.nucleoid.extras.lobby.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.extras.lobby.NEBlocks;

import java.util.function.Consumer;

public class ContributorStatueBlock extends BaseEntityBlock implements PolymerBlock, BlockWithElementHolder, TooltipProvider {
    protected static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ContributorStatueBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide() && player.canUseGameMasterBlocks()) {
            var blockEntity = world.getBlockEntity(pos, NEBlocks.CONTRIBUTOR_STATUE_ENTITY);

            if (blockEntity.isPresent()) {
                blockEntity.get().openEditScreen((ServerPlayer) player);
                return InteractionResult.SUCCESS_SERVER;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.SMOOTH_STONE.defaultBlockState();
    }

    @Override
    public ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new ContributorStatueModel();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var facing = context.getHorizontalDirection().getOpposite();
        return super.getStateForPlacement(context).setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addToTooltip(TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type, DataComponentGetter components) {
        // Todo
        /*var nbt = components.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, null).copyNbtWithoutId().getNbt();

        if (nbt != null) {
            var contributorId = nbt.getString(ContributorStatueBlockEntity.CONTRIBUTOR_ID_KEY, "");
            var contributor = ContributorData.getContributor(contributorId);

            if (contributor != null) {
                textConsumer.accept(Text.translatable("block.nucleoid_extras.contributor_statue.contributor", contributor.getName()).formatted(Formatting.GRAY));
            }

            if (type.isAdvanced()) {
                textConsumer.accept(Text.translatable("block.nucleoid_extras.contributor_statue.contributor_id", contributorId).formatted(Formatting.GRAY));
            }
        }*/
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ContributorStatueBlockEntity(pos, state);
    }
}
