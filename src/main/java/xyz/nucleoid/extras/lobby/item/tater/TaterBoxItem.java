package xyz.nucleoid.extras.lobby.item.tater;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.TaterGuiElementBuilder;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterSelectionComponent;
import xyz.nucleoid.extras.lobby.NEItems;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.CorruptaterBlock;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.gui.TaterBoxGui;
import xyz.nucleoid.extras.tag.NEBlockTags;
import xyz.nucleoid.server.translations.api.Localization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TaterBoxItem extends Item implements PolymerItem {
    private static final Component NOT_OWNER_MESSAGE = Component.translatable("text.nucleoid_extras.tater_box.not_owner").withStyle(ChatFormatting.RED);
    public static final Component NONE_TEXT = Component.translatable("text.nucleoid_extras.tater_box.none");

    private static final String LEGACY_TATERS_KEY = "Taters";
    private static final int COLOR = 0xCEADAA;

    public TaterBoxItem(Properties settings) {
        super(settings.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                .setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
                .build()));
    }

    private MutableComponent getTitle(ServerPlayer player, ItemStack stack) {
        Component name = this.getName(stack);
        int count = PlayerLobbyState.get(player).collectedTaters.size();
        long max = getCollectableTaterCount(player.registryAccess());

        return Component.translatable("text.nucleoid_extras.tater_box.title", name, count, max);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (!user.level().isClientSide()) {
            this.openTaterBox((ServerPlayer) user, stack, hand);
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        if (clickType == ClickAction.SECONDARY && !player.level().isClientSide()) {
            this.openTaterBox((ServerPlayer) player, stack, null);
            return true;
        }

        return false;
    }

    private void openTaterBox(ServerPlayer user, ItemStack stack, InteractionHand hand) {
        if (NEItems.canUseTaters(user)) {
            this.migrateCollectedTaters(user, stack);
            this.openTaterBoxUi(user, stack, hand);
        }
    }

    private void migrateCollectedTaters(ServerPlayer user, ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, customData -> {
                if (!customData.copyTag().contains(LEGACY_TATERS_KEY)) {
                    return customData;
                }

                return customData.update(nbt -> {
                    var data = PlayerLobbyState.get(user);

                    for (var e : nbt.getListOrEmpty(LEGACY_TATERS_KEY)) {
                        if (e instanceof StringTag entry) {
                            var block = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(entry.value()));

                            if (block instanceof TinyPotatoBlock tinyPotatoBlock) {
                                data.collectedTaters.add(tinyPotatoBlock);
                            }
                        }
                    }

                    nbt.remove(LEGACY_TATERS_KEY);
                    user.sendSystemMessage(Component.translatable("text.nucleoid_extras.tater_box.updated"));
                });
            });
        }
    }

    private void openTaterBoxUi(ServerPlayer user, ItemStack stack, InteractionHand hand) {
        if (stack.has(NEDataComponentTypes.TATER_SELECTION)) {
            var state = PlayerLobbyState.get(user);
            List<GuiElement> taters = new ArrayList<>();

            taters.add(createGuiElement(stack, user, hand, Items.BARRIER, NONE_TEXT, null, true, true));

            getSortedTaterStream(user)
                .map(tater -> {
                    boolean found = state.collectedTaters.contains(tater);

                    return createGuiElement(stack, user, hand, tater, tater.getName(), tater.builtInRegistryHolder(), found, tater.isCollectable());
                })
                .forEachOrdered(taters::add);

            var ui = TaterBoxGui.of(user, taters, this.isCreative());
            ui.setHideUnfound(true);
            ui.setTitle(this.getTitle(user, stack));
            ui.open();

            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);

            if (equippable != null) {
                user.connection.send(new ClientboundSoundPacket(equippable.equipSound(), SoundSource.PLAYERS, user.getX(), user.getY(), user.getZ(), 0.8f, 1, user.getRandom().nextLong()));
            }
        }
    }

    private TaterBoxGui.TaterGuiElement createGuiElement(ItemStack stack, Player user, InteractionHand hand, ItemLike icon, Component text, Holder<Block> tater, boolean found, boolean collectable) {
        var guiElementBuilder = new TaterGuiElementBuilder(icon.asItem());
        guiElementBuilder.setName(text);
        guiElementBuilder.setRarity(Rarity.COMMON);
        guiElementBuilder.setFound(found);
        guiElementBuilder.setCollectable(collectable);
        guiElementBuilder.hideDefaultTooltip();
        guiElementBuilder.setCallback((index, type, action, gui) -> {
            ItemStack newStack = hand == null ? stack : user.getItemInHand(hand);
            if (found && this == newStack.getItem()) {
                newStack.update(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT, taterSelection -> taterSelection.selected(tater));
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
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack itemStack, PacketContext context, HolderLookup.Provider provider) {
        PolymerItem.super.modifyBasePolymerItemStack(out, itemStack, context, provider);
        Optional<Holder<Block>> selectedTater = itemStack.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT).tater();
        if (selectedTater.isPresent() && selectedTater.get().value() instanceof TinyPotatoBlock potatoBlock) {
            ResolvableProfile profile = PolymerUtils.createProfileComponent(potatoBlock.getItemTexture());
            out.set(DataComponents.PROFILE, profile);
        } else {
            out.set(DataComponents.DYED_COLOR, new DyedItemColor(this.getEmptyColor()));
            out.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            out.set(DataComponents.EQUIPPABLE, Items.LEATHER_HELMET.components().get(DataComponents.EQUIPPABLE));
        }
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, textConsumer, type);

        var owner = PolymerCommonUtils.getPlayer(PacketContext.get());

        Optional<Holder<Block>> selectedBlock = stack.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT).tater();
        Component selectedName;

        if (selectedBlock.isPresent()) {
            selectedName = selectedBlock.get().value().getName();
        } else {
            selectedName = NONE_TEXT;
        }

        textConsumer.accept(Component.translatable("text.nucleoid_extras.tater_box.selected", selectedName).withStyle(ChatFormatting.GRAY));

        int count;
        if (owner != null) count = PlayerLobbyState.get(owner).collectedTaters.size();
        else count = 0;
        long max = getCollectableTaterCount(context.registries());
        String percent = String.format("%.2f", max == 0 ? 0 : count / (double) max * 100);

        textConsumer.accept(Component.translatable("text.nucleoid_extras.tater_box.completion", count, max, percent).withStyle(ChatFormatting.GRAY));
    }

    public static Stream<TinyPotatoBlock> getCollectableTaters(HolderLookup.Provider registries) {
        return registries
            .lookupOrThrow(Registries.BLOCK)
            .get(NEBlockTags.COLLECTABLE_TATERS)
            .map(HolderSet::stream)
            .orElseGet(Stream::empty)
            .map(Holder::value)
            .filter(block -> block instanceof TinyPotatoBlock)
            .map(block -> (TinyPotatoBlock) block);
    }

    public static long getCollectableTaterCount(HolderLookup.Provider registries) {
        return getCollectableTaters(registries).count();
    }

    public static Stream<TinyPotatoBlock> getSortedTaterStream(ServerPlayer player) {
        return TinyPotatoBlock.TATERS.stream()
            .sorted(Comparator.comparing(tater -> {
                if (!(tater instanceof CorruptaterBlock)) {
                    var name = tater.getName();

                    if (name != null) {
                        return Localization.component(name, player).getString();
                    }
                }

                return BuiltInRegistries.BLOCK.getKey(tater).getPath();
            }, String.CASE_INSENSITIVE_ORDER));
    }
}
