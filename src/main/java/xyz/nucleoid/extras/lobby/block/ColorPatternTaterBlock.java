package xyz.nucleoid.extras.lobby.block;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class ColorPatternTaterBlock extends TinyPotatoBlock {
    private final ParticleEffect[] particleEffects;

    public ColorPatternTaterBlock(Settings settings, Vec3f[] pattern, String texture) {
        super(settings, (ParticleEffect) null, texture);

        this.particleEffects = Stream.of(pattern).map(color -> {
            return new DustParticleEffect(color, 1);
        }).toArray(ParticleEffect[]::new);
    }

    @Override
    public ParticleEffect getParticleEffect(int time) {
        return this.particleEffects[(time / 10) % this.particleEffects.length];
    }
}
