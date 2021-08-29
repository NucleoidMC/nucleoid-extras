package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.item.VirtualItem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.lobby.NEEntities;
import xyz.nucleoid.extras.lobby.entity.QuickArmorStandEntity;

import java.util.Random;

public class QuickArmorStandItem extends Item implements VirtualItem {
    public QuickArmorStandItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof ArmorStandEntity armorStandEntity) {
            var quickArmorStand = new QuickArmorStandEntity(armorStandEntity.world);
            quickArmorStand.readNbt(armorStandEntity.writeNbt(new NbtCompound()));
            armorStandEntity.remove(Entity.RemovalReason.DISCARDED);
            quickArmorStand.world.spawnEntity(quickArmorStand);
            return true;
        }
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
            BlockPos blockPos = itemPlacementContext.getBlockPos();
            ItemStack itemStack = context.getStack();
            Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
            Box box = NEEntities.QUICK_ARMOR_STAND_ENTITY_TYPE.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
            if (world.isSpaceEmpty(null, box, (entity) -> true) && world.getOtherEntities(null, box).isEmpty()) {
                if (world instanceof ServerWorld serverWorld) {
                    var armorStandEntity = NEEntities.QUICK_ARMOR_STAND_ENTITY_TYPE.create(serverWorld, itemStack.getTag(), null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
                    if (armorStandEntity == null) {
                        return ActionResult.FAIL;
                    }

                    float f = (float) MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    armorStandEntity.refreshPositionAndAngles(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0F);
                    this.setRotations(armorStandEntity, world.random);
                    serverWorld.spawnEntityAndPassengers(armorStandEntity);
                    world.playSound(null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, armorStandEntity);
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.FAIL;
            }
        }
    }

    private void setRotations(ArmorStandEntity stand, Random random) {
        EulerAngle eulerAngle = stand.getHeadRotation();
        float f = random.nextFloat() * 5.0F;
        float g = random.nextFloat() * 20.0F - 10.0F;
        EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
        stand.setHeadRotation(eulerAngle2);
        eulerAngle = stand.getBodyRotation();
        f = random.nextFloat() * 10.0F - 5.0F;
        eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
        stand.setBodyRotation(eulerAngle2);
    }

    @Override
    public Item getVirtualItem() {
        return Items.ARMOR_STAND;
    }

    @Override
    public ItemStack getVirtualItemStack(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        var out = VirtualItem.super.getVirtualItemStack(itemStack, player);
        out.addEnchantment(Enchantments.LOYALTY, 23);
        return out;
    }
}
