package xyz.nucleoid.extras.lobby.block;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class ColorTaterBlock extends TinyPotatoBlock {
    public ColorTaterBlock(Settings settings, Vec3f color, String texture) {
        super(settings, new DustParticleEffect(color, 1), texture);
    }

    public ColorTaterBlock(Settings settings, DyeColor color, String texture) {
        this(settings, new Vec3f(Vec3d.unpackRgb(color.getSignColor())), texture);
    }
}
