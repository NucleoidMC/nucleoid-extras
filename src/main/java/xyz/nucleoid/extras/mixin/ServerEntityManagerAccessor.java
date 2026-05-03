package xyz.nucleoid.extras.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;

@Mixin(PersistentEntitySectionManager.class)
public interface ServerEntityManagerAccessor {
    @Accessor("knownUuids")
    Set<UUID> nucleoid$getEntityUuids();
}
