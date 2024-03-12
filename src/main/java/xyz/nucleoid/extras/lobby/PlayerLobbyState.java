package xyz.nucleoid.extras.lobby;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.mixin.lobby.ArmorStandEntityAccessor;
import xyz.nucleoid.extras.tag.NEBlockTags;

import java.util.HashSet;
import java.util.Set;

public class PlayerLobbyState {

    public static final PlayerDataStorage<PlayerLobbyState> STORAGE = new JsonDataStorage<>("nucleoid_extras", PlayerLobbyState.class);
    public final Set<TinyPotatoBlock> collectedTaters = new HashSet<>();

    public ActionResult collectTaterFromBlock(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ActionResult result = this.collectTater(block, stack, player);

        if (isFickle(result, block, player)) {
            world.breakBlock(pos, false);
        }

        return result;
    }

    public ActionResult collectTaterFromEntity(Entity entity, Vec3d hitPos, ItemStack stack, PlayerEntity player) {
        if (entity instanceof ArmorStandEntity armorStand) {
            EquipmentSlot slot = ((ArmorStandEntityAccessor) (Object) armorStand).callSlotFromPosition(hitPos);
            return this.collectTaterFromSlot(armorStand.getEquippedStack(slot), stack, player);
        } else if (entity instanceof PlayerEntity targetPlayer) {
            ItemStack targetStack = targetPlayer.getEquippedStack(EquipmentSlot.HEAD);
            
            if (targetStack.getItem() instanceof TaterBoxItem) {
                Block targetTater = TaterBoxItem.getSelectedTater(targetStack);

                if (targetTater != null && targetTater.getDefaultState().isIn(NEBlockTags.VIRAL_TATERS)) {
                    return this.collectTater(targetTater, stack, player);
                }
            }
        }

        return ActionResult.PASS;
    }

    private ActionResult collectTaterFromSlot(ItemStack slotStack, ItemStack stack, PlayerEntity player) {
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

    private ActionResult collectTater(Block block, ItemStack stack, PlayerEntity player) {
        if (!(block instanceof TinyPotatoBlock tater)) return ActionResult.PASS;

        if (stack.getItem() instanceof TaterBoxItem) {
            stack.getOrCreateNbt().putUuid(TaterBoxItem.OWNER_KEY, player.getUuid());
        }

        boolean alreadyAdded = this.collectedTaters.contains(tater);
        Text message;

        if (alreadyAdded) {
            message = Text.translatable("text.nucleoid_extras.tater_box.already_added", block.getName()).formatted(Formatting.RED);
        } else {
            this.collectedTaters.add(tater);

            message = Text.translatable("text.nucleoid_extras.tater_box.added", block.getName());
        }

        player.sendMessage(message, true);
        triggerCollectCriterion((ServerPlayerEntity) player, Registries.BLOCK.getId(tater), this.collectedTaters.size());

        return alreadyAdded ? ActionResult.FAIL : ActionResult.SUCCESS;
    }

    private static void triggerCollectCriterion(ServerPlayerEntity player, Identifier taterId, int count) {
        NECriteria.TATER_COLLECTED.trigger(player, taterId, count);
    }

    private static boolean isFickle(ActionResult result, Block block, PlayerEntity player) {
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
