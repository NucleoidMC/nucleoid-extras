package xyz.nucleoid.extras.lobby.item.tater;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterSelectionComponent;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.CorruptaterBlock;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.gui.TaterBoxGui;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.server.translations.api.Localization;

import java.util.*;
import java.util.stream.Stream;

public class TaterBoxItem extends Item implements PolymerItem {
    private static final Text NOT_OWNER_MESSAGE = Text.translatable("text.nucleoid_extras.tater_box.not_owner").formatted(Formatting.RED);
    public static final Text NONE_TEXT = Text.translatable("text.nucleoid_extras.tater_box.none");

    private static final String LEGACY_TATERS_KEY = "Taters";
    private static final int COLOR = 0xCEADAA;

    public TaterBoxItem(Settings settings) {
        super(settings.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD)
                .equipSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER)
                .build()));
    }

    private MutableText getTitle(ServerPlayerEntity player) {
        Text name = this.getName();
        int count = PlayerLobbyState.get(player).collectedTaters.size();
        int max = TinyPotatoBlock.TATERS.size();

        return Text.translatable("text.nucleoid_extras.tater_box.title", name, count, max);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!user.getWorld().isClient()) {
            this.openTaterBox(world, (ServerPlayerEntity) user, stack, hand);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && !player.getWorld().isClient()) {
            this.openTaterBox(player.getWorld(), (ServerPlayerEntity) player, stack, null);
            return true;
        }

        return false;
    }

    private void openTaterBox(World world, ServerPlayerEntity user, ItemStack stack, Hand hand) {
        if (NEItems.canUseTaters(user) && stack.contains(NEDataComponentTypes.TATER_SELECTION)) {
            stack.apply(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT, customData -> {
                if (!customData.contains(LEGACY_TATERS_KEY)) {
                    return customData;
                }

                return customData.apply(nbt -> {
                    var data = PlayerLobbyState.get(user);

                    for (var entry : nbt.getList(LEGACY_TATERS_KEY, NbtElement.STRING_TYPE)) {
                        var block = Registries.BLOCK.get(Identifier.tryParse(entry.asString()));

                        if (block instanceof TinyPotatoBlock tinyPotatoBlock) {
                            data.collectedTaters.add(tinyPotatoBlock);
                        }
                    }

                    nbt.remove(LEGACY_TATERS_KEY);
                    user.sendMessage(Text.translatable("text.nucleoid_extras.tater_box.updated"));
                });
            });

            var state = PlayerLobbyState.get(user);
            List<GuiElementInterface> taters = new ArrayList<>();

            taters.add(createGuiElement(stack, user, hand, Items.BARRIER, NONE_TEXT, null, true));

            getSortedTaterStream(user)
                .map(tater -> {
                    boolean found = state.collectedTaters.contains(tater);

                    return createGuiElement(stack, user, hand, tater, tater.getName(), tater.getRegistryEntry(), found);
                })
                .forEachOrdered(taters::add);

            var ui = TaterBoxGui.of(user, taters, this.isCreative());
            ui.setHideUnfound(true);
            ui.setTitle(this.getTitle(user));
            ui.open();

            EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);

            if (equippable != null) {
                user.networkHandler.sendPacket(new PlaySoundS2CPacket(equippable.equipSound(), SoundCategory.PLAYERS, user.getX(), user.getY(), user.getZ(), 0.8f, 1, user.getRandom().nextLong()));
            }
        }
    }

    private TaterBoxGui.TaterGuiElement createGuiElement(ItemStack stack, PlayerEntity user, Hand hand, ItemConvertible icon, Text text, RegistryEntry<Block> tater, boolean found) {
        var guiElementBuilder = new TaterBoxGui.TaterGuiElementBuilder(icon.asItem());
        guiElementBuilder.setName(text);
        guiElementBuilder.setRarity(Rarity.COMMON);
        guiElementBuilder.setFound(found);
        guiElementBuilder.hideDefaultTooltip();
        guiElementBuilder.setCallback((index, type, action, gui) -> {
            ItemStack newStack = hand == null ? stack : user.getStackInHand(hand);
            if (found && this == newStack.getItem()) {
                newStack.apply(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT, taterSelection -> taterSelection.selected(tater));
                gui.close();
            }
        });

        TaterSelectionComponent taterSelection = stack.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT);

        if (Optional.ofNullable(tater).equals(taterSelection.tater())) {
            guiElementBuilder.glow();
        }

        return guiElementBuilder.build();
    }

    /**
     * {@return the color used for the empty leather helmet appearance}
     */
    protected int getEmptyColor() {
        return COLOR;
    }

    protected boolean isCreative() {
        return false;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (itemStack.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT).tater().isPresent()) {
            return Items.PLAYER_HEAD;
        } else {
            return Items.LEATHER_HELMET;
        }
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);

        Optional<RegistryEntry<Block>> selectedTater = itemStack.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT).tater();
        if (selectedTater.isPresent() && selectedTater.get().value() instanceof TinyPotatoBlock potatoBlock) {
            ProfileComponent profile = PolymerUtils.createProfileComponent(potatoBlock.getItemTexture());
            out.set(DataComponentTypes.PROFILE, profile);
        } else {
            out.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(this.getEmptyColor(), false));
            out.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(false));
            out.set(DataComponentTypes.EQUIPPABLE, Items.LEATHER_HELMET.getComponents().get(DataComponentTypes.EQUIPPABLE));
        }

        return out;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        var owner = PacketContext.get();

        Optional<RegistryEntry<Block>> selectedBlock = stack.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT).tater();
        Text selectedName;

        if (selectedBlock.isPresent()) {
            selectedName = selectedBlock.get().value().getName();
        } else {
            selectedName = NONE_TEXT;
        }

        tooltip.add(Text.translatable("text.nucleoid_extras.tater_box.selected", selectedName).formatted(Formatting.GRAY));

        int count = owner != null && owner.getPlayer() != null ? PlayerLobbyState.get(owner.getPlayer()).collectedTaters.size() : 0;
        int max = CubicPotatoBlock.TATERS.size();
        String percent = String.format("%.2f", count / (double) max * 100);

        tooltip.add(Text.translatable("text.nucleoid_extras.tater_box.completion", count, max, percent).formatted(Formatting.GRAY));
    }

    public static Stream<TinyPotatoBlock> getSortedTaterStream(ServerPlayerEntity player) {
        return TinyPotatoBlock.TATERS.stream()
            .sorted(Comparator.comparing(tater -> {
                if (!(tater instanceof CorruptaterBlock)) {
                    var name = tater.getName();

                    if (name != null) {
                        return Localization.text(name, player).getString();
                    }
                }

                return Registries.BLOCK.getId(tater).getPath();
            }, String.CASE_INSENSITIVE_ORDER));
    }
}
