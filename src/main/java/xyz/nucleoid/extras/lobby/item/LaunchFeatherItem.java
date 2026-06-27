package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.lobby.block.LaunchPadBlock;

public class LaunchFeatherItem extends Item implements PolymerItem {
    public LaunchFeatherItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        LauncherComponent launcher = stack.get(NEDataComponentTypes.LAUNCHER);

        if (!user.level().isClientSide() && LaunchPadBlock.tryLaunch(entity, user, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, launcher)) {
            return InteractionResult.SUCCESS_SERVER;
        }

        return InteractionResult.PASS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.FEATHER;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider provider) {
        return null;
    }
}
