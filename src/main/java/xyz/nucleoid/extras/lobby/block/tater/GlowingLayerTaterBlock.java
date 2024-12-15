package xyz.nucleoid.extras.lobby.block.tater;

import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class GlowingLayerTaterBlock extends CubicPotatoBlock implements BlockWithElementHolder {
    private final Pixel[] glowingPixels;

    public GlowingLayerTaterBlock(Settings settings, ParticleEffect particleEffect, String texture, Pixel[] glowingPixels) {
        super(settings, particleEffect, texture, 1);
        this.glowingPixels = glowingPixels;
    }

    @Override
    public ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        var holder = new ElementHolder();

        int rotation = initialBlockState.get(Properties.ROTATION);

        for (var pixel : this.glowingPixels) {
            holder.addElement(pixel.createDisplayElement(rotation));
        }

        return holder;
    }

    public record Pixel(int x, int y, int color) {
        private static final int CREAKING_ORANGE = 0xFFEC7214;
        private static final int CREAKING_YELLOW = 0xFFEFA337;

        public static final Pixel[] CREAKING = {
            new Pixel(3, 1, CREAKING_ORANGE),
            new Pixel(4, 1, CREAKING_ORANGE),
            new Pixel(2, 2, CREAKING_ORANGE),
            new Pixel(5, 2, CREAKING_ORANGE),

            new Pixel(5, 4, CREAKING_YELLOW),
            new Pixel(2, 5, CREAKING_YELLOW),
        };

        public DisplayElement createDisplayElement(int rotation) {
            var element = new TextDisplayElement();

            float fullRotation = MathHelper.PI * -2;

            var transformation = new Matrix4f()
                .rotateY(MathHelper.PI)
                .rotateY(rotation / 16f * fullRotation)
                .translate(-0.25f, -0.5f, 0.25f)
                .translate(x / 16f, y / 16f, 0)
                .scale(83/100f, 25/100f, 1)
                .translate(1/40f,0,0.001f);

            element.setTransformation(transformation);
            element.setText(Text.literal(".").styled(style -> style.withColor(color)));

            element.setBackground(color);
            element.setBrightness(Brightness.FULL);

            element.setDisplaySize(1, 1);
            element.setInvisible(true);

            return element;
        }
    }
}
