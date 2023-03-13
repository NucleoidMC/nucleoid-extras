package xyz.nucleoid.extras.lobby.block;

import java.util.List;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;

public class ContributorStatueBlock extends BlockWithEntity implements PolymerBlock {
    protected static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public ContributorStatueBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && player.isCreativeLevelTwoOp()) {
            var blockEntity = world.getBlockEntity(pos, NEBlocks.CONTRIBUTOR_STATUE_ENTITY);

            if (blockEntity.isPresent()) {
                blockEntity.get().openEditScreen((ServerPlayerEntity) player);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.SMOOTH_STONE;
    }

    @Override
    public void onPolymerBlockSend(BlockState state, BlockPos.Mutable pos, ServerPlayerEntity player) {
        player.getWorld().getBlockEntity(pos, NEBlocks.CONTRIBUTOR_STATUE_ENTITY).ifPresent(ContributorStatueBlockEntity::spawnHolograms);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        var facing = context.getPlayerFacing().getOpposite();
        return super.getPlacementState(context).with(FACING, facing);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        super.appendTooltip(stack, world, tooltip, options);

        var nbt = BlockItem.getBlockEntityNbt(stack);

        if (nbt != null) {
            var contributorId = nbt.getString(ContributorStatueBlockEntity.CONTRIBUTOR_ID_KEY);
            var contributor = ContributorData.getContributor(contributorId);

            if (contributor != null) {
                tooltip.add(Text.translatable("block.nucleoid_extras.contributor_statue.contributor", contributor.getName()).formatted(Formatting.GRAY));
            }

            if (options.isAdvanced()) {
                tooltip.add(Text.translatable("block.nucleoid_extras.contributor_statue.contributor_id", contributorId).formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ContributorStatueBlockEntity(pos, state);
    }
}
