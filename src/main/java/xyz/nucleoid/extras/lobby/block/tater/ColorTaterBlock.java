package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.DyeColor;

public class ColorTaterBlock extends CubicPotatoBlock {
    public ColorTaterBlock(Settings settings, int color, String texture) {
        super(settings, new DustParticleEffect(color, 1), texture);
    }

    public ColorTaterBlock(Settings settings, DyeColor color, String texture) {
        this(settings, color.getSignColor(), texture);
    }
}
