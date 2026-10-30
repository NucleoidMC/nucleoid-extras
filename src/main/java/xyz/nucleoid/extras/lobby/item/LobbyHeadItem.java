package xyz.nucleoid.extras.lobby.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import xyz.nucleoid.extras.component.NEDataComponentTypes;

import java.util.function.Consumer;

public class LobbyHeadItem extends BlockItem implements PolymerItem {
    private final String texture;

    @SuppressWarnings("unchecked")
    public LobbyHeadItem(Block block, Properties settings, String texture) {
        super(block, settings.component(NEDataComponentTypes.LOBBY_WARNING, Unit.INSTANCE));
        this.texture = texture;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.PLAYER_HEAD;
    }


    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context, HolderLookup.Provider lookup) {
        PolymerItem.super.modifyBasePolymerItemStack(out, stack, context, lookup);
        out.set(DataComponents.PROFILE, PolymerUtils.createProfileComponent(this.texture));
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider provider) {
        return null;
    }
}
