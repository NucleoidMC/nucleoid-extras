package xyz.nucleoid.extras.lobby.item.tater;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.sgui.api.elements.BookElementBuilder;
import eu.pb4.sgui.api.gui.BookGui;
import net.minecraft.SharedConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.mixin.lobby.ThreadedAnvilChunkStorageAccessor;

public class TaterGuidebookItem extends Item implements PolymerItem {
    private static final Text MISSING_SYMBOL = Text.literal("❌").formatted(Formatting.RED);
    private static final Text FOUND_SYMBOL = Text.literal("✔").formatted(Formatting.GREEN);
    private static final Text TOO_MANY_SYMBOL = Text.literal("✔").setStyle(Style.EMPTY.withColor(0x055005));

    private static final int RECORD_COOLDOWN = 2 * SharedConstants.TICKS_PER_SECOND;

    private static final String TATER_POSITIONS_KEY = "tater_positions";

    public TaterGuidebookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (!world.isClient() && user.isCreativeLevelTwoOp()) {
            var player = (ServerPlayerEntity) user;
            var taterPositionMap = getTaterPositions(stack);

            if (user.isSneaking()) {
                recordToGuidebook(player, taterPositionMap, stack);
            } else {
                showGuidebook(player, taterPositionMap, stack);
            }

            return TypedActionResult.success(stack, world.isClient());
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public Item getPolymerItem(ItemStack stack, ServerPlayerEntity player) {
        return Items.WRITTEN_BOOK;
    }

    private static void recordToGuidebook(ServerPlayerEntity player, SetMultimap<Item, BlockPos> taterPositions, ItemStack stack) {
        int initialCount = taterPositions.size();

        var pos = new BlockPos.Mutable();
        var chunkManager = player.getServerWorld().getChunkManager();

        var chunkStorage = chunkManager.threadedAnvilChunkStorage;
        var accessor = (ThreadedAnvilChunkStorageAccessor) (Object) chunkStorage;

        for (var holder : accessor.callEntryIterator()) {
            var chunk = holder.getWorldChunk();

            if (chunk != null) {
                recordChunk(chunk, pos, taterPositions);
            }
        }

        setTaterPositions(stack, taterPositions);

        player.getItemCooldownManager().set(stack.getItem(), RECORD_COOLDOWN);

        int difference = taterPositions.size() - initialCount;
        player.sendMessage(Text.translatable("text.nucleoid_extras.tater_guidebook.recorded", difference), true);
    }

    private static void recordChunk(Chunk chunk, BlockPos.Mutable pos, SetMultimap<Item, BlockPos> taterPositions) {
        for (int z = 0; z < 16; z++) {
            for (int y = chunk.getBottomY(); y < chunk.getTopY(); y++) {
                for (int x = 0; x < 16; x++) {
                    pos.set(x, y, z);
                    var state = chunk.getBlockState(pos);

                    if (state.getBlock() instanceof TinyPotatoBlock taterBlock) {
                        taterPositions.put(taterBlock.asItem(), chunk.getPos().getBlockPos(x, y, z));
                    }
                }
            }
        }
    }

    private static void showGuidebook(ServerPlayerEntity player, SetMultimap<Item, BlockPos> taterPositionMap, ItemStack stack) {
        var builder = new BookElementBuilder();
        var taters = TaterBoxItem.getSortedTaterStream(player).iterator();

        boolean firstPage = true;

        while (taters.hasNext()) {
            var page = Text.empty();

            if (firstPage) {
                page.append(stack.getName().copy().formatted(Formatting.BOLD));
                page.append(ScreenTexts.LINE_BREAK);

                page.append(Text.translatable("text.nucleoid_extras.tater_guidebook.header", taterPositionMap.size()));
                page.append(ScreenTexts.LINE_BREAK);
                page.append(ScreenTexts.LINE_BREAK);

                firstPage = false;
            }

            for (int index = 0; index < (16 * 4); index++) {
                if (!taters.hasNext()) break;

                var tater = taters.next().asItem();
                var positions = taterPositionMap.get(tater);

                var symbol = getSymbol(positions);

                var hoverEvent = getHoverEvent(tater, positions);
                var clickEvent = getClickEvent(tater, positions);

                var text = symbol.copy().styled(style -> {
                    return style
                        .withHoverEvent(hoverEvent)
                        .withClickEvent(clickEvent);
                });

                page.append(text);
            }

            builder.addPage(page);
        }

        var ui = new BookGui(player, builder);
        ui.open();
    }

    private static Text getSymbol(Set<BlockPos> positions) {
        return switch (positions.size()) {
            case 0 -> MISSING_SYMBOL;
            case 1 -> FOUND_SYMBOL;
            default -> TOO_MANY_SYMBOL;
        };
    }

    private static HoverEvent getHoverEvent(Item tater, Set<BlockPos> positions) {
        var hoverText = tater.getName().copy();

        for (var pos : positions) {
            hoverText.append(ScreenTexts.LINE_BREAK);

            Text coordinates = Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ());
            hoverText.append(Texts.bracketed(coordinates).formatted(Formatting.GREEN));
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
    }

    private static ClickEvent getClickEvent(Item tater, Set<BlockPos> positions) {
        if (positions.isEmpty()) return null;

        var pos = positions.iterator().next();
        var command = "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
    }

    private static SetMultimap<Item, BlockPos> getTaterPositions(ItemStack stack) {
        var map = HashMultimap.<Item, BlockPos>create();
        var nbt = stack.getSubNbt(TATER_POSITIONS_KEY);

        if (nbt != null) {
            for (var key : nbt.getKeys()) {
                var id = Identifier.tryParse(key);
                if (id == null) continue;

                var item = Registries.ITEM.get(id);
                if (item == null) continue;

                var list = nbt.getList(key, NbtElement.COMPOUND_TYPE);

                for (int index = 0; index < list.size(); index++) {
                    var pos = list.getCompound(index);
                    map.put(item, NbtHelper.toBlockPos(pos));
                }
            }
        }

        return map;
    }

    private static void setTaterPositions(ItemStack stack, SetMultimap<Item, BlockPos> taterPositions) {
        var nbt = stack.getOrCreateSubNbt(TATER_POSITIONS_KEY);

        taterPositions.asMap().forEach((item, positions) -> {
            var list = new NbtList();

            for (var pos : positions) {
                list.add(NbtHelper.fromBlockPos(pos));
            }

            nbt.put(Registries.ITEM.getId(item).toString(), list);
        });
    }
}
