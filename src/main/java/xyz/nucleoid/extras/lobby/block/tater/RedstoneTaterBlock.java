package xyz.nucleoid.extras.lobby.block.tater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawner;
import xyz.nucleoid.extras.lobby.particle.TaterParticleSpawnerTypes;

public class RedstoneTaterBlock extends CubicPotatoBlock {
	public static final MapCodec<RedstoneTaterBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				createSettingsCodec(),
				TaterParticleSpawnerTypes.CODEC.fieldOf("particle_spawner").forGetter(RedstoneTaterBlock::getParticleSpawner),
				Codec.STRING.fieldOf("texture").forGetter(RedstoneTaterBlock::getItemTexture)
		).apply(instance, RedstoneTaterBlock::new)
	);

	public RedstoneTaterBlock(Settings settings, TaterParticleSpawner particleSpawner, String texture) {
		super(settings, particleSpawner, texture);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 15;
	}

    @Override
    public MapCodec<? extends RedstoneTaterBlock> getCodec() {
        return CODEC;
    }
}
