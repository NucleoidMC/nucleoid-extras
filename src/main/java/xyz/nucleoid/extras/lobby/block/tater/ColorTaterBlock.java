package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class ColorTaterBlock extends CubicPotatoBlock {
    public ColorTaterBlock(Settings settings, Vector3f color, String texture) {
        super(settings, new DustParticleEffect(color, 1), texture);
    }

    public ColorTaterBlock(Settings settings, DyeColor color, String texture) {
        this(settings, Vec3d.unpackRgb(color.getSignColor()).toVector3f(), texture);
    }
}
