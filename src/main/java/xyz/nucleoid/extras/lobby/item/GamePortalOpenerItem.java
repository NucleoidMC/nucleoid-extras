package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import xyz.nucleoid.extras.component.GamePortalComponent;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.plasmid.impl.portal.GamePortal;

public class GamePortalOpenerItem extends Item implements PolymerItem {
    public GamePortalOpenerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            GamePortal portal = getGamePortal(stack);
            if (portal == null) {
                return ActionResult.FAIL;
            }

            portal.requestJoin((ServerPlayerEntity) user, false);
        }

        return ActionResult.SUCCESS_SERVER;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        GamePortal portal = getGamePortal(stack);
        return portal == null ? Items.CLOCK : portal.getIcon().getItem();
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public Text getName(ItemStack stack) {
        GamePortal portal = getGamePortal(stack);
        return portal == null ? super.getName(stack) : portal.getName();
    }

    private static GamePortal getGamePortal(ItemStack stack) {
        GamePortalComponent component = stack.get(NEDataComponentTypes.GAME_PORTAL);
        return component == null ? null : component.getGamePortal();
    }
}
