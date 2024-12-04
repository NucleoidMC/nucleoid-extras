package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import xyz.nucleoid.extras.tag.NEBlockTags;
import xyz.nucleoid.extras.util.SkinEncoder;

import java.util.ArrayList;
import java.util.List;

public abstract class TinyPotatoBlock extends Block implements PolymerBlock {
    public static final Codec<RegistryEntry<Block>> ENTRY_CODEC = Registries.BLOCK.getEntryCodec().validate(block -> {
        if (block.value() instanceof TinyPotatoBlock) {
            return DataResult.success(block);
        }

        return DataResult.error(() -> "Not a tater: " + block);
    });

    public static final List<TinyPotatoBlock> TATERS = new ArrayList<>();

    private final ParticleEffect particleEffect;
    private final int particleRate;
    private final String texture;

    public TinyPotatoBlock(Settings settings, String texture, ParticleEffect particleEffect, int particleRate) {
        super(settings);
        this.particleEffect = particleEffect;
        this.particleRate = particleRate;
        this.texture = SkinEncoder.encode(texture);
        TATERS.add(this);
    }

    public ParticleEffect getParticleEffect(int time) {
        return this.particleEffect;
    }

    public ParticleEffect getBlockParticleEffect(BlockState state, ServerWorld world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return this.getParticleEffect(world.getServer().getTicks());
    }

    public void spawnBlockParticles(ServerWorld world, BlockPos pos, ParticleEffect particleEffect) {
        if (particleEffect != null && world.getRandom().nextInt(getBlockParticleChance()) == 0) {
            world.spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    1, 0.5, 0.5, 0.5, 0.2);
        }
    }

    public ParticleEffect getPlayerParticleEffect(ServerPlayerEntity player) {
        return this.getParticleEffect(player.getServer().getTicks());
    }

    public int getBlockParticleChance() {
        return 1;
    }

    public int getPlayerParticleRate(ServerPlayerEntity player) {
        return particleRate;
    }

    public void spawnPlayerParticles(ServerPlayerEntity player) {
        Box box = player.getBoundingBox();

        double deltaX = box.getLengthX() / 2d;
        double deltaY = box.getLengthY() / 2d;
        double deltaZ = box.getLengthZ() / 2d;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        ParticleEffect particleEffect = this.getPlayerParticleEffect(player);
        if (particleEffect != null) {
            player.getServerWorld().spawnParticles(particleEffect, x, y, z, 1, deltaX, deltaY, deltaZ, 0.2);
        }
    }

    /**
     * {@return whether the block should be removed after the tater is collected}
     */
    public boolean isFickle() {
        return false;
    }

    public boolean isCollectable() {
        return this.getDefaultState().isIn(NEBlockTags.COLLECTABLE_TATERS);
    }

    public final String getItemTexture() {
        return this.texture;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            ParticleEffect particleEffect = this.getBlockParticleEffect(state, serverWorld, pos, player, hit);
            this.spawnBlockParticles(serverWorld, pos, particleEffect);
        }

        return ActionResult.SUCCESS_SERVER;
    }
}
