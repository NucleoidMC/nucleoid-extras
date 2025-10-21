package xyz.nucleoid.extras.mixin.compat;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract World getEntityWorld();

    @Shadow
    public abstract Vec3d getEntityPos();

    // getEntityWorld 1.21.5
    public World method_5770() {
        return this.getEntityWorld();
    }

    // getWorld 1.21.8
    public World method_37908() {
        return this.getEntityWorld();
    }

    // getPos 1.21.8
    public Vec3d method_19538() {
        return this.getEntityPos();
    }

    // getServer 1.21.8
    public MinecraftServer method_5682() {
        if (getEntityWorld() instanceof ServerWorld world) {
            return world.getServer();
        }

        return null;
    }
}
