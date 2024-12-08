package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.extras.lobby.block.ContainerLockAccess;

public class LockSetterItem extends SimplePolymerItem {
    public LockSetterItem(Settings settings) {
        super(settings, Items.TRIAL_KEY, false);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();
        var user = context.getPlayer();

        if (!world.isClient() && user.isCreativeLevelTwoOp()) {
            var stack = context.getStack();
            var newLock = stack.get(DataComponentTypes.LOCK);

            if (newLock != null) {
                var pos = context.getBlockPos();
                var blockEntity = world.getBlockEntity(pos);

                if (blockEntity instanceof ContainerLockAccess access) {
                    var currentLock = access.getContainerLock();

                    if (currentLock == ContainerLock.EMPTY) {
                        access.setContainerLock(newLock);
                        sendFeedback(user, access, "locked");
                    } else if (!newLock.equals(currentLock)) {
                        sendFeedback(user, access, "already_locked");
                        return ActionResult.FAIL;
                    } else {
                        access.setContainerLock(ContainerLock.EMPTY);
                        sendFeedback(user, access, "unlocked");
                    }

                    return ActionResult.SUCCESS_SERVER;
                }
            }
        }

        return ActionResult.PASS;
    }

    private static void sendFeedback(PlayerEntity player, ContainerLockAccess access, String suffix) {
        var text = Text.translatable("text.nucleoid_extras.lock_setter." + suffix, access.getContainerLockName());
        player.sendMessage(text, true);

        player.playSoundToPlayer(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1, 1);
    }

    public static ContainerLock createUnlockableLock() {
        var predicate = ItemPredicate.Builder.create()
                .count(IntRange.exactly(-1))
                .build();

        return new ContainerLock(predicate);
    }
}
