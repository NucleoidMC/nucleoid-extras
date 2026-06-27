package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import xyz.nucleoid.extras.component.GamePortalComponent;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.model.NEModels;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.plasmid.impl.portal.GamePortal;

public class GamePortalOpenerItem extends Item implements PolymerItem {
    public GamePortalOpenerItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (!world.isClientSide()) {
            GamePortal portal = getGamePortal(stack);
            if (portal == null) {
                return InteractionResult.FAIL;
            }

            portal.requestJoin((ServerPlayer) user, false);
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        GamePortal portal = getGamePortal(stack);
        return portal == null ? Items.CLOCK : portal.getIcon().getItem();
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider provider) {
        if (PolymerResourcePackUtils.hasMainPack(context)) {
            return NEModels.CONTROLLER;
        }
        return null;
    }

    @Override
    public Component getName(ItemStack stack) {
        GamePortal portal = getGamePortal(stack);
        return portal == null ? super.getName(stack) : portal.getName();
    }

    private static GamePortal getGamePortal(ItemStack stack) {
        GamePortalComponent component = stack.get(NEDataComponentTypes.GAME_PORTAL);
        return component == null ? null : component.getGamePortal();
    }
}
