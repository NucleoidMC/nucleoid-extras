package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.item.DyeColor;

public class ColorTaterBlock extends CubicPotatoBlock {
    public ColorTaterBlock(Properties settings, int color, String texture) {
        super(settings, new DustParticleOptions(color, 1), texture);
    }

    public ColorTaterBlock(Properties settings, DyeColor color, String texture) {
        this(settings, color.getTextColor(), texture);
    }
}
