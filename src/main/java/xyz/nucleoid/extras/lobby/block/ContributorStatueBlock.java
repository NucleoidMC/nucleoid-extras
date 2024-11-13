package xyz.nucleoid.extras.lobby.block;

import java.util.List;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;
import xyz.nucleoid.packettweaker.PacketContext;

public class ContributorStatueBlock extends BlockWithEntity implements PolymerBlock, BlockWithElementHolder {
    protected static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public ContributorStatueBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient() && player.isCreativeLevelTwoOp()) {
            var blockEntity = world.getBlockEntity(pos, NEBlocks.CONTRIBUTOR_STATUE_ENTITY);

            if (blockEntity.isPresent()) {
                blockEntity.get().openEditScreen((ServerPlayerEntity) player);
                return ActionResult.SUCCESS_SERVER;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.SMOOTH_STONE.getDefaultState();
    }

    @Override
    public ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new ContributorStatueModel();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        var facing = context.getHorizontalPlayerFacing().getOpposite();
        return super.getPlacementState(context).with(FACING, facing);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        var nbt = stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).getNbt();

        if (nbt != null) {
            var contributorId = nbt.getString(ContributorStatueBlockEntity.CONTRIBUTOR_ID_KEY);
            var contributor = ContributorData.getContributor(contributorId);

            if (contributor != null) {
                tooltip.add(Text.translatable("block.nucleoid_extras.contributor_statue.contributor", contributor.getName()).formatted(Formatting.GRAY));
            }

            if (type.isAdvanced()) {
                tooltip.add(Text.translatable("block.nucleoid_extras.contributor_statue.contributor_id", contributorId).formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ContributorStatueBlockEntity(pos, state);
    }
}
