package xyz.nucleoid.extras.lobby.entity;

import eu.pb4.polymer.api.entity.PolymerEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEEntities;

public class QuickArmorStandEntity extends ArmorStandEntity implements PolymerEntity {
    public QuickArmorStandEntity(EntityType<? extends ArmorStandEntity> entityType, World world) {
        super(entityType, world);
    }

    public QuickArmorStandEntity(World world) {
        super(NEEntities.QUICK_ARMOR_STAND_ENTITY_TYPE, world);
    }

    @Override
    public EntityType<?> getPolymerEntityType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        var attacker = damageSource.getAttacker();
        if (attacker instanceof ServerPlayerEntity player && player.interactionManager.isSurvivalLike()) {
            return true;
        }
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    protected void tickCramming() {

    }

    @Override
    public void tickMovement() {

    }
}
