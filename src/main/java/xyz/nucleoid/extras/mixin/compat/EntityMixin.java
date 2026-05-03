package xyz.nucleoid.extras.mixin.compat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 position();

    // getEntityWorld 1.21.5
    public Level method_5770() {
        return this.level();
    }

    // getWorld 1.21.8
    public Level method_37908() {
        return this.level();
    }

    // getPos 1.21.8
    public Vec3 method_19538() {
        return this.position();
    }

    // getServer 1.21.8
    public MinecraftServer method_5682() {
        if (level() instanceof ServerLevel world) {
            return world.getServer();
        }

        return null;
    }
}
