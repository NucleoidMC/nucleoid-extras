package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.math.Axis;
import eu.pb4.factorytools.api.util.LazyItemStack;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xyz.nucleoid.extras.util.SkinEncoder;

public class BotanicalPotatoBlock extends TinyPotatoBlock implements BlockWithElementHolder {
    private final LazyItemStack upStack;
    private final LazyItemStack downStack;

    public BotanicalPotatoBlock(Properties settings, String upperTexture, String lowerTexture, ParticleOptions particleEffect, int particleRate) {
        super(settings.noOcclusion(), upperTexture, particleEffect, particleRate);
        this.upStack = new LazyItemStack(() -> PolymerUtils.createPlayerHead(this.getItemTexture()));
        this.downStack = new LazyItemStack(() -> PolymerUtils.createPlayerHead(SkinEncoder.encode(lowerTexture)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.ROTATION_16);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.ROTATION_16, Mth.floor(RotationSegment.convertToSegment(ctx.getRotation())));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    @Override
    public boolean tickElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return true;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        var model = (Model) BlockBoundAttachment.get(world, pos).holder();

        if (model.jumpTime < 0) {
            model.jumpTime = 20;
        }

        return super.useWithoutItem(state, world, pos, player, hit);
    }

    private class Model extends ElementHolder {
        private final ItemDisplayElement upPart;
        private final ItemDisplayElement downPart;

        private int jumpTime = 0;
        private BlockState state;
        private final Matrix4f mat = new Matrix4f();

        Model(BlockState state) {
            this.upPart = new ItemDisplayElement(BotanicalPotatoBlock.this.upStack.get());
            this.downPart = new ItemDisplayElement(BotanicalPotatoBlock.this.downStack.get());

            this.upPart.setItemDisplayContext(ItemDisplayContext.FIXED);
            this.downPart.setItemDisplayContext(ItemDisplayContext.FIXED);

            this.upPart.setInterpolationDuration(1);
            this.downPart.setInterpolationDuration(1);

            this.upPart.setInvisible(true);
            this.downPart.setInvisible(true);

            this.state = state;
            this.updateAnimation();

            this.addElement(this.upPart);
            this.addElement(this.downPart);
        }

        @Override
        protected void onTick() {
            if (this.jumpTime >= 0) {
                this.updateAnimation();
            }

            this.jumpTime--;
        }

        private void updateAnimation() {
            mat.identity();
            mat.rotateY(-RotationSegment.convertToDegrees(state.getValue(BlockStateProperties.ROTATION_16)) * Mth.DEG_TO_RAD);

            if (this.jumpTime > 0) {
                // Math stolen from botania™
                // https://github.com/VazkiiMods/Botania/blob/bd5c644356fa0456efc3773c8829517f1f2c5808/Xplat/src/main/java/vazkii/botania/client/render/block_entity/TinyPotatoBlockEntityRenderer.java#L139
                float up = (float) Math.abs(Math.sin(this.jumpTime / 10f * Math.PI)) * 0.2F;
                float rotZ = (float) Math.sin(this.jumpTime / 10f * Math.PI) * 2;
                float wiggle = (float) Math.sin(this.jumpTime / 10f * Math.PI) * 0.05F;

                mat.translate(wiggle, up, 0F);
                mat.rotate(Axis.ZP.rotationDegrees(rotZ));
            }
            mat.rotateY(Mth.PI);

            this.upPart.setTransformation(mat);
            mat.translate(new Vector3f(0, -0.25f, 0));
            this.downPart.setTransformation(mat);

            this.upPart.startInterpolation();
            this.downPart.startInterpolation();
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            if (updateType == BlockBoundAttachment.BLOCK_STATE_UPDATE) {
                this.state = BlockBoundAttachment.get(this).getBlockState();
                updateAnimation();
            }
        }
    }
}
