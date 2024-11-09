package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.entity.LeaderboardDisplayEntity;
import xyz.nucleoid.extras.lobby.entity.QuickArmorStandEntity;

public class NEEntities {
    public static final EntityType<QuickArmorStandEntity> QUICK_ARMOR_STAND =
            register("quick_armor_stand", EntityType.Builder
                    .<QuickArmorStandEntity>create(QuickArmorStandEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 1.975f)
                    .maxTrackingRange(2)
                    .trackingTickInterval(10));


    public static final EntityType<LeaderboardDisplayEntity> LEADERBOARD_DISPLAY =
            register("leaderboard_display", EntityType.Builder
                    .create(LeaderboardDisplayEntity::new, SpawnGroup.MISC)
                    .dimensions(0f, 0f)
                    .maxTrackingRange(2)
                    .trackingTickInterval(10));


    public static void register() {
        FabricDefaultAttributeRegistry.register(QUICK_ARMOR_STAND, QuickArmorStandEntity.createLivingAttributes());
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, NucleoidExtras.identifier(id));
        EntityType<T> type = builder.build(key);

        PolymerEntityUtils.registerType(type);
        return Registry.register(Registries.ENTITY_TYPE, key, type);
    }
}
