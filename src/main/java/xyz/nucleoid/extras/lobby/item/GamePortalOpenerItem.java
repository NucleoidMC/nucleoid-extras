package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import xyz.nucleoid.plasmid.game.portal.GamePortal;
import xyz.nucleoid.plasmid.game.portal.GamePortalManager;

public class GamePortalOpenerItem extends Item implements PolymerItem {
    private static final String GAME_PORTAL_KEY = "GamePortal";

    public GamePortalOpenerItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            GamePortal portal = getGamePortal(stack);
            if (portal == null) {
                return TypedActionResult.fail(stack);
            }

            portal.requestJoin((ServerPlayerEntity) user);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public Item getPolymerItem(ItemStack stack, ServerPlayerEntity player) {
        GamePortal portal = getGamePortal(stack);
        return portal == null ? Items.CLOCK : portal.getIcon().getItem();
    }

    @Override
    public Text getName(ItemStack stack) {
        GamePortal portal = getGamePortal(stack);
        return portal == null ? super.getName(stack) : portal.getName();
    }

    private static GamePortal getGamePortal(ItemStack stack) {
        return GamePortalManager.INSTANCE.byId(getGamePortalId(stack));
    }

    private static Identifier getGamePortalId(ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();

            if (nbt.contains(GAME_PORTAL_KEY, NbtType.STRING)) {
                String string = nbt.getString(GAME_PORTAL_KEY);
                return Identifier.tryParse(string);
            }
        }

        return null;
    }

    public static void setGamePortalId(ItemStack stack, Identifier id) {
        stack.getOrCreateNbt().putString(GAME_PORTAL_KEY, id.toString());
    }
}
