package xyz.nucleoid.extras.lobby.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import eu.pb4.polymer.api.block.PolymerHeadBlock;
import eu.pb4.polymer.api.item.PolymerItem;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.lobby.block.TinyPotatoBlock;
import xyz.nucleoid.plasmid.util.Guis;

public class TaterBoxItem extends ArmorItem implements PolymerItem {
    private static final Text NOT_OWNER_MESSAGE = new TranslatableText("text.nucleoid_extras.tater_box.not_owner").formatted(Formatting.RED);

    private static final String OWNER_KEY = "Owner";
    private static final String TATERS_KEY = "Taters";
    private static final String SELECTED_TATER_KEY = "SelectedTater";
    private static final int COLOR = 0xCEADAA;

    public TaterBoxItem(Settings settings) {
        super(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, settings);
    }

    private ActionResult isOwner(ItemStack stack, PlayerEntity player) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) return ActionResult.PASS;
        
        if (!tag.contains(OWNER_KEY, NbtElement.LIST_TYPE)) return ActionResult.PASS;
        UUID uuid = tag.getUuid(OWNER_KEY);
        if (uuid == null) return ActionResult.PASS;

        return player.getUuid().equals(uuid) ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    private int getBlockCount(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) return 0;
        if (!tag.contains(TATERS_KEY, NbtElement.LIST_TYPE)) return 0;

        int count = 0;
        NbtList taters = tag.getList(TATERS_KEY, NbtElement.STRING_TYPE);
        for (int index = 0; index < taters.size(); index++) {
            Identifier blockId = Identifier.tryParse(taters.getString(index));
            if (blockId != null) {
                count++;
            }
        }

        return count;
    }

    private Iterator<Identifier> getBlockIds(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) return Collections.emptyIterator();
        if (!tag.contains(TATERS_KEY, NbtElement.LIST_TYPE)) return Collections.emptyIterator();

        Set<Identifier> blockIds = new HashSet<>();
        NbtList taters = tag.getList(TATERS_KEY, NbtElement.STRING_TYPE);
        for (int index = 0; index < taters.size(); index++) {
            Identifier blockId = Identifier.tryParse(taters.getString(index));
            if (blockId != null) {
                blockIds.add(blockId);
            }
        }

        return blockIds.stream().sorted().iterator();
    }

    private MutableText getTitle(ItemStack stack) {
        Text name = this.getName(stack);
        int count = this.getBlockCount(stack);
        int max = TinyPotatoBlock.TATERS.size();

        return new TranslatableText("text.nucleoid_extras.tater_box.title", name, count, max);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            BlockHitResult hit = Item.raycast(world, user, RaycastContext.FluidHandling.NONE);
            if (hit.getType() == HitResult.Type.BLOCK) {
                this.tryAdd(world, hit.getBlockPos(), stack, user);
            } else {
                List<GuiElementInterface> taters = new ArrayList<>();

                Iterator<Identifier> iterator = this.getBlockIds(stack);
                while (iterator.hasNext()) {
                    Identifier blockId = iterator.next();

                    Block block = Registry.BLOCK.get(blockId);

                    var tater = new GuiElementBuilder(block.asItem());
                    tater.setName(block.getName());
                    tater.hideFlags();
                    tater.setCallback((index, type, action, gui) -> {
                        ItemStack newStack = user.getStackInHand(hand);
                        if (this == newStack.getItem() && this.isOwner(newStack, user) != ActionResult.FAIL) {
                            TaterBoxItem.setSelectedTater(newStack, blockId);
                            gui.close();
                        }
                    });
                    
                    taters.add(tater.build());
                }

                var ui = Guis.createSelectorGui((ServerPlayerEntity) user, this.getTitle(stack), false, taters);
                ui.open();
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    private ActionResult tryAdd(World world, BlockPos pos, ItemStack stack, PlayerEntity player) {
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof TinyPotatoBlock)) return ActionResult.PASS;

        Identifier id = Registry.BLOCK.getId(block);

        ActionResult owner = this.isOwner(stack, player);
        if (owner == ActionResult.FAIL) {
            player.sendMessage(NOT_OWNER_MESSAGE, true);
            return ActionResult.FAIL;
        }
        
        NbtCompound tag = stack.getOrCreateNbt();
        if (owner == ActionResult.PASS) {
            tag.putUuid(OWNER_KEY, player.getUuid());
        }

        NbtList taters = tag.getList(TATERS_KEY, NbtElement.STRING_TYPE);
        for (int index = 0; index < taters.size(); index++) {
            String string = taters.getString(index);
            if (id.toString().equals(string)) {
                Text message = new TranslatableText("text.nucleoid_extras.tater_box.already_added", block.getName()).formatted(Formatting.RED);
                player.sendMessage(message, true);

                return ActionResult.FAIL;
            }
        }

        taters.add(NbtString.of(id.toString()));
        tag.put(TATERS_KEY, taters);

        Text message = new TranslatableText("text.nucleoid_extras.tater_box.added", block.getName());
        player.sendMessage(message, true);

        return ActionResult.SUCCESS;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (TaterBoxItem.getSelectedTater(itemStack) instanceof PolymerHeadBlock) {
            return Items.PLAYER_HEAD;
        } else {
            return Items.LEATHER_HELMET;
        }
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        ItemStack out = PolymerItem.super.getPolymerItemStack(itemStack, player);

        Block selectedTater = TaterBoxItem.getSelectedTater(itemStack);
        if (selectedTater instanceof PolymerHeadBlock polymerHeadBlock) {
            NbtCompound skullOwner = polymerHeadBlock.getPolymerHeadSkullOwner(selectedTater.getDefaultState());
            out.getOrCreateNbt().put(SkullItem.SKULL_OWNER_KEY, skullOwner);
        } else {
            out.getOrCreateSubNbt(DyeableItem.DISPLAY_KEY).putInt(DyeableItem.COLOR_KEY, COLOR);
            out.getOrCreateNbt().putBoolean("Unbreakable", true);
        }

        return out;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        int count = this.getBlockCount(stack);
        int max = TinyPotatoBlock.TATERS.size();
        String percent = String.format("%.2f", count / (double) max * 100);

        tooltip.add(new TranslatableText("text.nucleoid_extras.tater_box.completion", count, max, percent).formatted(Formatting.GRAY));
    }

    public static Block getSelectedTater(ItemStack stack) {
        NbtCompound tag = stack.getNbt();
        if (tag == null) return null;

        Identifier selectedTaterId = Identifier.tryParse(tag.getString(SELECTED_TATER_KEY));
        if (selectedTaterId == null) return null;

        return Registry.BLOCK.get(selectedTaterId);
    }

    public static void setSelectedTater(ItemStack stack, Identifier selectedTaterId) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putString(SELECTED_TATER_KEY, selectedTaterId.toString());
    }
}
