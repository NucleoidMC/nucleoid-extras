package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.entity.EntityHelper;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.entity.QuickArmorStandEntity;

public class NEEntities {
    public static final EntityType<QuickArmorStandEntity> QUICK_ARMOR_STAND_ENTITY_TYPE =
            FabricEntityTypeBuilder.<QuickArmorStandEntity>create(SpawnGroup.MISC, QuickArmorStandEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 1.975f))
                    .trackRangeChunks(2)
                    .trackedUpdateRate(10)
                    .build();


    public static void register() {
        register("quick_armor_stand", QUICK_ARMOR_STAND_ENTITY_TYPE);
        FabricDefaultAttributeRegistry.register(QUICK_ARMOR_STAND_ENTITY_TYPE, QuickArmorStandEntity.createLivingAttributes());
    }

    private static <T extends EntityType<?>> T register(String id, T item) {
        EntityHelper.registerVirtualEntityType(item);
        return Registry.register(Registry.ENTITY_TYPE, NucleoidExtras.identifier(id), item);
    }
}
