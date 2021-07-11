package xyz.nucleoid.extras.mixin;

import net.minecraft.server.world.ServerEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(ServerEntityManager.class)
public interface ServerEntityManagerAccessor {
    @Accessor("entityUuids")
    Set<UUID> nucleoid$getEntityUuids();
}
