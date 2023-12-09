package xyz.nucleoid.extras.lobby.item.tater;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.NucleoidExtras;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.CorruptaterBlock;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.gui.TaterBoxGui;
import xyz.nucleoid.server.translations.api.Localization;

import java.util.*;
import java.util.stream.Stream;

public class TaterBoxItem extends Item implements PolymerItem, Equipment {
    private static final Text NOT_OWNER_MESSAGE = Text.translatable("text.nucleoid_extras.tater_box.not_owner").formatted(Formatting.RED);
    public static final Text NONE_TEXT = Text.translatable("text.nucleoid_extras.tater_box.none");

    public static final String OWNER_KEY = "Owner";
    private static final String LEGACY_TATERS_KEY = "Taters";
    private static final String SELECTED_TATER_KEY = "SelectedTater";
    private static final int COLOR = 0xCEADAA;

    private static final Identifier VIRAL_TATERS_ID = NucleoidExtras.identifier("viral_taters");
    public static final TagKey<Block> VIRAL_TATERS = TagKey.of(RegistryKeys.BLOCK, VIRAL_TATERS_ID);

    public TaterBoxItem(Settings settings) {
        super(settings);
    }

    private ActionResult isOwner(ItemStack stack, PlayerEntity player) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) return ActionResult.PASS;
        
        if (!tag.contains(OWNER_KEY, NbtElement.LIST_TYPE)) return ActionResult.PASS;
        UUID uuid = tag.getUuid(OWNER_KEY);
        if (uuid == null) return ActionResult.PASS;

        return player.getUuid().equals(uuid) ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    private MutableText getTitle(ServerPlayerEntity player) {
        Text name = this.getName();
        int count = PlayerLobbyState.get(player).collectedTaters.size();
        int max = TinyPotatoBlock.TATERS.size();

        return Text.translatable("text.nucleoid_extras.tater_box.title", name, count, max);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!user.getWorld().isClient()) {
            this.openTaterBox(world, (ServerPlayerEntity) user, stack, hand);
        }

        return TypedActionResult.success(stack, world.isClient());
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
        if (!world.isClient()) {
            if (stack.hasNbt() && stack.getNbt().contains(LEGACY_TATERS_KEY)) {
                var data = PlayerLobbyState.get(user);

                for (var nbt : stack.getNbt().getList(LEGACY_TATERS_KEY, NbtElement.STRING_TYPE)) {
                    var block = Registries.BLOCK.get(Identifier.tryParse(nbt.asString()));

                    if (block instanceof TinyPotatoBlock tinyPotatoBlock) {
                        data.collectedTaters.add(tinyPotatoBlock);
                    }
                }
                stack.getNbt().remove(LEGACY_TATERS_KEY);
                user.sendMessage(Text.translatable("text.nucleoid_extras.tater_box.updated"));
            }

            var state = PlayerLobbyState.get(user);
            List<GuiElementInterface> taters = new ArrayList<>();

            taters.add(createGuiElement(stack, user, hand, Items.BARRIER, NONE_TEXT, null, true));

            getSortedTaterStream(user)
                .map(tater -> {
                    boolean found = state.collectedTaters.contains(tater);

                    return createGuiElement(stack, user, hand, tater, tater.getName(), Registries.BLOCK.getId(tater), found);
                })
                .forEachOrdered(taters::add);

            var ui = TaterBoxGui.of((ServerPlayerEntity) user, taters, this.isCreative());
            ui.setHideUnfound(true);
            ui.setTitle(this.getTitle((ServerPlayerEntity) user));
            ui.open();

            ((ServerPlayerEntity) user).playSound(this.getEquipSound(), SoundCategory.PLAYERS, 0.8f, 1);
        }
    }

    private TaterBoxGui.TaterGuiElement createGuiElement(ItemStack stack, PlayerEntity user, Hand hand, ItemConvertible icon, Text text, Identifier taterId, boolean found) {
        var guiElementBuilder = new TaterBoxGui.TaterGuiElementBuilder(icon.asItem());
        guiElementBuilder.setName(text);
        guiElementBuilder.setFound(found);
        guiElementBuilder.hideFlags();
        guiElementBuilder.setCallback((index, type, action, gui) -> {
            ItemStack newStack = hand == null ? stack : user.getStackInHand(hand);
            if (found && this == newStack.getItem() && this.isOwner(newStack, user) != ActionResult.FAIL) {
                TaterBoxItem.setSelectedTater(newStack, taterId);
                gui.close();
            }
        });
        if(Objects.equals(getSelectedTaterId(stack), taterId)) guiElementBuilder.glow();
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
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (TaterBoxItem.getSelectedTater(itemStack) != null) {
            return Items.PLAYER_HEAD;
        } else {
            return Items.LEATHER_HELMET;
        }
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, context, player);

        Block selectedTater = TaterBoxItem.getSelectedTater(itemStack);
        if (selectedTater instanceof TinyPotatoBlock potatoBlock) {
            NbtCompound skullOwner = PolymerUtils.createSkullOwner(potatoBlock.getItemTexture());
            out.getOrCreateNbt().put(PlayerHeadItem.SKULL_OWNER_KEY, skullOwner);
        } else {
            out.getOrCreateSubNbt(DyeableItem.DISPLAY_KEY).putInt(DyeableItem.COLOR_KEY, this.getEmptyColor());
            out.getOrCreateNbt().putBoolean("Unbreakable", true);
        }

        return out;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        var owner = getOwnerPlayer(stack, world);

        Block selectedBlock = getSelectedTater(stack);
        Text selectedName;

        if (selectedBlock != null) {
            selectedName = selectedBlock.getName();
        } else {
            selectedName = NONE_TEXT;
        }

        tooltip.add(Text.translatable("text.nucleoid_extras.tater_box.selected", selectedName).formatted(Formatting.GRAY));

        int count = owner != null ? PlayerLobbyState.get(owner).collectedTaters.size() : 0;
        int max = CubicPotatoBlock.TATERS.size();
        String percent = String.format("%.2f", count / (double) max * 100);

        tooltip.add(Text.translatable("text.nucleoid_extras.tater_box.completion", count, max, percent).formatted(Formatting.GRAY));
    }

    private PlayerEntity getOwnerPlayer(ItemStack stack, World world) {
        if (!stack.hasNbt() || !stack.getNbt().contains(OWNER_KEY)) {
            return null;
        }

        var owner = stack.getNbt().getUuid(OWNER_KEY);

        return world.getPlayerByUuid(owner);
    }

    @Nullable
    public static Identifier getSelectedTaterId(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains(SELECTED_TATER_KEY)) return null;

        return Identifier.tryParse(tag.getString(SELECTED_TATER_KEY));
    }

	@Nullable
    public static Block getSelectedTater(ItemStack stack) {
        Identifier id = getSelectedTaterId(stack);
        if(id == null) return null;

        return Registries.BLOCK.get(id);
    }

    public static void setSelectedTater(ItemStack stack, @Nullable Identifier selectedTaterId) {
        NbtCompound tag = stack.getOrCreateNbt();
        if(selectedTaterId == null) {
            tag.remove(SELECTED_TATER_KEY);
        } else {
            tag.putString(SELECTED_TATER_KEY, selectedTaterId.toString());
        }
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
