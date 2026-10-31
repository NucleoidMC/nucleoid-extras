package xyz.nucleoid.extras.lobby;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
    public boolean shouldShowAlreadyCollectedText = false;

    public InteractionResult collectTaterFromBlock(Level world, BlockPos pos, ItemStack stack, ServerPlayer player) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        InteractionResult result = this.collectTater(block, stack, player);

        if (isFickle(result, block, player)) {
            world.destroyBlock(pos, false);
        }

        return result;
    }

    public InteractionResult collectTaterFromEntity(Entity entity, Vec3 hitPos, ItemStack stack, ServerPlayer player) {
        if (entity instanceof ArmorStand armorStand) {
            EquipmentSlot slot = ((ArmorStandEntityAccessor) (Object) armorStand).callSlotFromPosition(hitPos);
            return this.collectTaterFromSlot(armorStand.getItemBySlot(slot), stack, player);
        } else if (entity instanceof Player targetPlayer) {
            ItemStack targetStack = targetPlayer.getItemBySlot(EquipmentSlot.HEAD);
            TaterSelectionComponent taterSelection = targetStack.get(NEDataComponentTypes.TATER_SELECTION);
            
            if (taterSelection != null && taterSelection.allowViralCollection() && taterSelection.tater().isPresent()) {
                Block targetTater = taterSelection.tater().get().value();

                if (targetTater.defaultBlockState().is(NEBlockTags.VIRAL_TATERS)) {
                    return this.collectTater(targetTater, stack, player);
                }
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult collectTaterFromSlot(ItemStack slotStack, ItemStack stack, ServerPlayer player) {
        if (!slotStack.isEmpty() && slotStack.getItem() instanceof BlockItem slotItem) {
            Block block = slotItem.getBlock();
            InteractionResult result = this.collectTater(block, stack, player);

            if (isFickle(result, block, player)) {
                slotStack.setCount(0);
            }

            return result;
        }

        return InteractionResult.PASS;
    }

    private InteractionResult collectTater(Block block, ItemStack stack, ServerPlayer player) {
        if (!NEItems.canUseTaters(player) || !(block instanceof TinyPotatoBlock tater) || !tater.isCollectable()) return InteractionResult.PASS;

        boolean alreadyAdded = this.collectedTaters.contains(tater);

        if (!alreadyAdded) {
            this.collectedTaters.add(tater);

            // Update the tooltip of tater boxes in player's inventory
            PolymerUtils.reloadInventory(player);

            player.sendSystemMessage(Component.translatable("text.nucleoid_extras.tater_box.added", block.getName()), true);
        } else if (shouldShowAlreadyCollectedText) {
            player.sendSystemMessage(Component.translatable("text.nucleoid_extras.tater_box.already_added", block.getName()), true);
        }

        triggerCollectCriterion(player, tater, this.collectedTaters.size());

        return alreadyAdded ? InteractionResult.PASS : InteractionResult.SUCCESS_SERVER;
    }

    private static void triggerCollectCriterion(ServerPlayer player, TinyPotatoBlock tater, int count) {
        NECriteria.TATER_COLLECTED.trigger(player, tater, count);
    }

    private static boolean isFickle(InteractionResult result, Block block, ServerPlayer player) {
        return result.consumesAction() && block instanceof TinyPotatoBlock tater && tater.isFickle() && !player.isCreative();
    }

    public static PlayerLobbyState get(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
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
