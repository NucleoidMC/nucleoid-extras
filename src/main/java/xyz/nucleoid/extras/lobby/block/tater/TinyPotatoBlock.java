package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import xyz.nucleoid.extras.tag.NEBlockTags;
import xyz.nucleoid.extras.util.SkinEncoder;

import java.util.ArrayList;
import java.util.List;

public abstract class TinyPotatoBlock extends Block implements PolymerBlock {
    public static final Codec<Holder<Block>> ENTRY_CODEC = BuiltInRegistries.BLOCK.holderByNameCodec().validate(block -> {
        if (block.value() instanceof TinyPotatoBlock) {
            return DataResult.success(block);
        }

        return DataResult.error(() -> "Not a tater: " + block);
    });

    public static final List<TinyPotatoBlock> TATERS = new ArrayList<>();

    private final ParticleOptions particleEffect;
    private final int particleRate;
    private final String texture;

    public TinyPotatoBlock(Properties settings, String texture, ParticleOptions particleEffect, int particleRate) {
        super(settings);
        this.particleEffect = particleEffect;
        this.particleRate = particleRate;
        this.texture = SkinEncoder.encode(texture);
        TATERS.add(this);
    }

    public ParticleOptions getParticleEffect(int time) {
        return this.particleEffect;
    }

    public ParticleOptions getBlockParticleEffect(BlockState state, ServerLevel world, BlockPos pos, Player player, BlockHitResult hit) {
        return this.getParticleEffect(world.getServer().getTickCount());
    }

    public void spawnBlockParticles(ServerLevel world, BlockPos pos, ParticleOptions particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.sendParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0.5, 0.5, 0.5, 0.2);
        }
    }

    public ParticleOptions getPlayerParticleEffect(ServerPlayer player) {
        return this.getParticleEffect(player.level().getServer().getTickCount());
    }

    public int getBlockParticleChance() {
        return 1;
    }

    public int getPlayerParticleRate(ServerPlayer player) {
        return particleRate;
    }

    public void spawnPlayerParticles(ServerPlayer player) {
        AABB box = player.getBoundingBox();

        double deltaX = box.getXsize() / 2d;
        double deltaY = box.getYsize() / 2d;
        double deltaZ = box.getZsize() / 2d;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        ParticleOptions particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.level().sendParticles(particleEffect, x, y, z, 1, deltaX, deltaY, deltaZ, 0.2);
        }
    }

    /**
     * {@return whether the block should be removed after the tater is collected}
     */
    public boolean isFickle() {
        return false;
    }

    public boolean isCollectable() {
        return this.defaultBlockState().is(NEBlockTags.COLLECTABLE_TATERS);
    }

    public final String getItemTexture() {
        return this.texture;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world instanceof ServerLevel serverWorld) {
            ParticleOptions particleEffect = this.getBlockParticleEffect(state, serverWorld, pos, player, hit);
            this.spawnBlockParticles(serverWorld, pos, particleEffect);
        }

        return InteractionResult.SUCCESS_SERVER;
    }
}
