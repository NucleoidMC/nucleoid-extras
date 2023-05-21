package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import xyz.nucleoid.extras.lobby.block.tater.LaunchPadBlock;
import xyz.nucleoid.extras.lobby.block.tater.LaunchPadBlockEntity;

public class LaunchFeatherItem extends Item implements PolymerItem {
    public LaunchFeatherItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        float pitch = LaunchPadBlockEntity.DEFAULT_PITCH;
        float power = LaunchPadBlockEntity.DEFAULT_POWER;

        NbtCompound nbt = stack.getNbt();

        if (nbt != null) {
            pitch = nbt.getFloat(LaunchPadBlockEntity.PITCH_KEY);
            power = nbt.getFloat(LaunchPadBlockEntity.POWER_KEY);
        }

        if (!user.getWorld().isClient() && LaunchPadBlock.tryLaunch(entity, user, SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, pitch, power)) {
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, ServerPlayerEntity player) {
        return Items.FEATHER;
    }
}
