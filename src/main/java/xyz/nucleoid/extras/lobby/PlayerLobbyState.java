package xyz.nucleoid.extras.lobby;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterSelectionComponent;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.mixin.lobby.ArmorStandEntityAccessor;
import xyz.nucleoid.extras.tag.NEBlockTags;

import java.util.HashSet;
import java.util.Set;

public class PlayerLobbyState {

    public static final PlayerDataStorage<PlayerLobbyState> STORAGE = new JsonDataStorage<>("nucleoid_extras", PlayerLobbyState.class);
    public final Set<TinyPotatoBlock> collectedTaters = new HashSet<>();

    public ActionResult collectTaterFromBlock(World world, BlockPos pos, ItemStack stack, ServerPlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ActionResult result = this.collectTater(block, stack, player);

        if (isFickle(result, block, player)) {
            world.breakBlock(pos, false);
        }

        return result;
    }

    public ActionResult collectTaterFromEntity(Entity entity, Vec3d hitPos, ItemStack stack, ServerPlayerEntity player) {
        if (entity instanceof ArmorStandEntity armorStand) {
            EquipmentSlot slot = ((ArmorStandEntityAccessor) (Object) armorStand).callSlotFromPosition(hitPos);
            return this.collectTaterFromSlot(armorStand.getEquippedStack(slot), stack, player);
        } else if (entity instanceof PlayerEntity targetPlayer) {
            ItemStack targetStack = targetPlayer.getEquippedStack(EquipmentSlot.HEAD);
            TaterSelectionComponent taterSelection = targetStack.get(NEDataComponentTypes.TATER_SELECTION);
            
            if (taterSelection != null && taterSelection.allowViralCollection() && taterSelection.tater().isPresent()) {
                Block targetTater = taterSelection.tater().get().value();

                if (targetTater.getDefaultState().isIn(NEBlockTags.VIRAL_TATERS)) {
                    return this.collectTater(targetTater, stack, player);
                }
            }
        }

        return ActionResult.PASS;
    }

    private ActionResult collectTaterFromSlot(ItemStack slotStack, ItemStack stack, ServerPlayerEntity player) {
        if (!slotStack.isEmpty() && slotStack.getItem() instanceof BlockItem slotItem) {
            Block block = slotItem.getBlock();
            ActionResult result = this.collectTater(block, stack, player);

            if (isFickle(result, block, player)) {
                slotStack.setCount(0);
            }

            return result;
        }

        return ActionResult.PASS;
    }

    private ActionResult collectTater(Block block, ItemStack stack, ServerPlayerEntity player) {
        if (!NEItems.canUseTaters(player) || !(block instanceof TinyPotatoBlock tater) || !tater.isCollectable()) return ActionResult.PASS;

        boolean alreadyAdded = this.collectedTaters.contains(tater);

        if (!alreadyAdded) {
            this.collectedTaters.add(tater);

            // Update the tooltip of tater boxes in player's inventory
            PolymerUtils.reloadInventory(player);

            player.sendMessage(Text.translatable("text.nucleoid_extras.tater_box.added", block.getName()), true);
        }

        triggerCollectCriterion(player, tater, this.collectedTaters.size());

        return alreadyAdded ? ActionResult.PASS : ActionResult.SUCCESS_SERVER;
    }

    private static void triggerCollectCriterion(ServerPlayerEntity player, TinyPotatoBlock tater, int count) {
        NECriteria.TATER_COLLECTED.trigger(player, tater, count);
    }

    private static boolean isFickle(ActionResult result, Block block, ServerPlayerEntity player) {
        return result.isAccepted() && block instanceof TinyPotatoBlock tater && tater.isFickle() && !player.isCreative();
    }

    public static PlayerLobbyState get(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return new PlayerLobbyState();
        }

        var data = PlayerDataApi.getCustomDataFor(serverPlayer, STORAGE);
        if (data == null) {
            data = new PlayerLobbyState();
            PlayerDataApi.setCustomDataFor(serverPlayer, STORAGE, data);
        }

        return data;
    }
}
