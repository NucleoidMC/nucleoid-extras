package xyz.nucleoid.extras.mixin.compat;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract World getWorld();

    // getEntityWorld 1.21.5
    public World method_5770() {
        return this.getWorld();
    }
}
