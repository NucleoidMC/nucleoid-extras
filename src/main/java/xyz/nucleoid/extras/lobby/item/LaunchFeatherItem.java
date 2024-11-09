package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlock;
import xyz.nucleoid.packettweaker.PacketContext;

public class LaunchFeatherItem extends Item implements PolymerItem {
    public LaunchFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        LauncherComponent launcher = stack.get(NEDataComponentTypes.LAUNCHER);

        if (!user.getWorld().isClient() && LaunchPadBlock.tryLaunch(entity, user, SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, launcher)) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.FEATHER;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }
}
