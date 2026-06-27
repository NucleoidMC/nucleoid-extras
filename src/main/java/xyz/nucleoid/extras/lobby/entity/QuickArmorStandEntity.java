package xyz.nucleoid.extras.lobby.entity;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xyz.nucleoid.extras.lobby.NEEntities;
import xyz.nucleoid.extras.lobby.NEItems;

public class QuickArmorStandEntity extends ArmorStand implements PolymerEntity {
    public QuickArmorStandEntity(EntityType<? extends ArmorStand> entityType, Level world) {
        super(entityType, world);
    }

    public QuickArmorStandEntity(Level world) {
        super(NEEntities.QUICK_ARMOR_STAND, world);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityTypes.ARMOR_STAND;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel world, DamageSource damageSource) {
        var attacker = damageSource.getEntity();
        if (attacker instanceof ServerPlayer player && player.gameMode.isSurvival()) {
            return true;
        }
        return super.isInvulnerableTo(world, damageSource);
    }

    @Override
    public ItemStack getPickResult() {
        return NEItems.QUICK_ARMOR_STAND.getDefaultInstance();
    }

    @Override
    protected void pushEntities() {

    }

    @Override
    public void aiStep() {

    }

    @Override
    public void setDeltaMovement(Vec3 velocity) {}
}
