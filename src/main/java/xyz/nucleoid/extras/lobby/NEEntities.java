package xyz.nucleoid.extras.lobby;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.entity.LeaderboardDisplayEntity;
import xyz.nucleoid.extras.lobby.entity.QuickArmorStandEntity;

public class NEEntities {
    public static final EntityType<QuickArmorStandEntity> QUICK_ARMOR_STAND =
            register("quick_armor_stand", EntityType.Builder
                    .<QuickArmorStandEntity>of(QuickArmorStandEntity::new, MobCategory.MISC)
                    .sized(0.5f, 1.975f)
                    .clientTrackingRange(2)
                    .updateInterval(10));


    public static final EntityType<LeaderboardDisplayEntity> LEADERBOARD_DISPLAY =
            register("leaderboard_display", EntityType.Builder
                    .of(LeaderboardDisplayEntity::new, MobCategory.MISC)
                    .sized(0f, 0f)
                    .clientTrackingRange(2)
                    .updateInterval(10));


    public static void register() {
        FabricDefaultAttributeRegistry.register(QUICK_ARMOR_STAND, QuickArmorStandEntity.createLivingAttributes());
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, NucleoidExtras.identifier(id));
        EntityType<T> type = builder.build(key);

        PolymerEntityUtils.registerType(type);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }
}
