package xyz.nucleoid.extras.mixin.compat;


import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("MissingUnique")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow
    public abstract <T extends ParticleEffect> int spawnParticles(T parameters, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed);

    @Shadow
    public abstract <T extends ParticleEffect> boolean spawnParticles(ServerPlayerEntity viewer, T parameters, boolean force, boolean important, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed);

    // spawnParticles from 1.21.3
    public <T extends ParticleEffect> int method_14199(T parameters, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        return this.spawnParticles(parameters, x, y, z, count, offsetX, offsetY, offsetZ, speed);
    }

    // spawnParticles from 1.21.3
    public <T extends ParticleEffect> boolean method_14166(ServerPlayerEntity viewer, T parameters, boolean force, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        return this.spawnParticles(viewer, parameters, force, false, x, y , z, count, offsetX, offsetY, offsetZ, speed);
    }

}
