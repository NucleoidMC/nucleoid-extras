package xyz.nucleoid.extras.mixin;

import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(PersistentEntitySectionManager.class)
public interface ServerEntityManagerAccessor {
    @Accessor("knownUuids")
    Set<UUID> nucleoid$getEntityUuids();
}
