package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.particle.TaterParticleContext;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawner;
import xyz.nucleoid.extras.util.SkinEncoder;

import java.util.ArrayList;
import java.util.List;

public abstract class TinyPotatoBlock extends Block implements PolymerBlock {
    public static final Codec<RegistryEntry<Block>> ENTRY_CODEC = Codecs.validate(Registries.BLOCK.createEntryCodec(), block -> {
        if (block.value() instanceof TinyPotatoBlock) {
            return DataResult.success(block);
        }

        return DataResult.error(() -> "Not a tater: " + block);
    });

    public static final List<TinyPotatoBlock> TATERS = new ArrayList<>();

    private final TaterParticleSpawner particleSpawner;
    private final String texture;

    public TinyPotatoBlock(Settings settings, TaterParticleSpawner particleSpawner, String texture) {
        super(settings);
        this.particleSpawner = particleSpawner;
        this.texture = SkinEncoder.encode(texture);
        TATERS.add(this);
    }

    public TaterParticleSpawner getParticleSpawner() {
        return this.particleSpawner;
    }

    /**
     * {@return whether the block should be removed after the tater is collected}
     */
    public boolean isFickle() {
        return false;
    }


    public final String getItemTexture() {
        return this.texture;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND) {
            return ActionResult.FAIL;
        }

        if (world instanceof ServerWorld serverWorld) {
            this.particleSpawner.trySpawn(new TaterParticleContext.Block(pos, serverWorld));
        }

        return ActionResult.SUCCESS;
    }
}
