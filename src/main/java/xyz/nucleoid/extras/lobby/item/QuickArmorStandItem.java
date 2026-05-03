package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xyz.nucleoid.extras.lobby.NEEntities;
import xyz.nucleoid.extras.lobby.entity.QuickArmorStandEntity;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Consumer;

public class QuickArmorStandItem extends Item implements PolymerItem {
    public QuickArmorStandItem(Properties settings) {
        super(settings);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof ArmorStand armorStandEntity) {
            var quickArmorStand = new QuickArmorStandEntity(armorStandEntity.level());
            var view = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, target.registryAccess());
            armorStandEntity.saveWithoutId(view);
            quickArmorStand.load(TagValueInput.create(ProblemReporter.DISCARDING, target.registryAccess(), view.buildResult()));
            armorStandEntity.remove(Entity.RemovalReason.DISCARDED);
            quickArmorStand.level().addFreshEntity(quickArmorStand);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level world = context.getLevel();
            BlockPlaceContext itemPlacementContext = new BlockPlaceContext(context);
            BlockPos blockPos = itemPlacementContext.getClickedPos();
            ItemStack itemStack = context.getItemInHand();
            Vec3 vec3d = Vec3.atBottomCenterOf(blockPos);
            AABB box = NEEntities.QUICK_ARMOR_STAND.getDimensions().makeBoundingBox(vec3d.x(), vec3d.y(), vec3d.z());
            if (world.noCollision(box) && world.getEntities(null, box).isEmpty()) {
                if (world instanceof ServerLevel serverWorld) {
                    var armorStandEntity = NEEntities.QUICK_ARMOR_STAND.spawn(serverWorld, itemStack, context.getPlayer(), blockPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
                    if (armorStandEntity == null) {
                        return InteractionResult.FAIL;
                    }

                    float f = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    armorStandEntity.snapTo(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0F);
                    this.setRotations(armorStandEntity, world.random);
                    world.playSound(null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                    world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, armorStandEntity.position());
                }

                itemStack.shrink(1);
                return InteractionResult.SUCCESS_SERVER;
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    private void setRotations(ArmorStand stand, RandomSource random) {
        Rotations eulerAngle = stand.getHeadPose();
        float f = random.nextFloat() * 5.0F;
        float g = random.nextFloat() * 20.0F - 10.0F;
        Rotations eulerAngle2 = new Rotations(eulerAngle.x() + f, eulerAngle.y() + g, eulerAngle.z());
        stand.setHeadPose(eulerAngle2);
        eulerAngle = stand.getBodyPose();
        f = random.nextFloat() * 10.0F - 5.0F;
        eulerAngle2 = new Rotations(eulerAngle.x(), eulerAngle.y() + f, eulerAngle.z());
        stand.setBodyPose(eulerAngle2);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.ARMOR_STAND;
    }

    @Override
    public ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);
        textConsumer.accept(Component.translatable("text.nucleoid_extras.lobby_items").setStyle(Style.EMPTY.withColor(ChatFormatting.RED).withItalic(false)));
    }
}
