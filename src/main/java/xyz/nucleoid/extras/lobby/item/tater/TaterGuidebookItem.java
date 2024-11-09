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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterPositionsComponent;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.extras.mixin.lobby.ServerChunkLoadingManagerAccessor;
import xyz.nucleoid.packettweaker.PacketContext;

public class TaterGuidebookItem extends Item implements PolymerItem {
    private static final Text MISSING_SYMBOL = Text.literal("❌").formatted(Formatting.RED);
    private static final Text FOUND_SYMBOL = Text.literal("✔").formatted(Formatting.GREEN);
    private static final Text TOO_MANY_SYMBOL = Text.literal("✔").setStyle(Style.EMPTY.withColor(0x055005));

    private static final int RECORD_COOLDOWN = 2 * SharedConstants.TICKS_PER_SECOND;

    public TaterGuidebookItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);

        if (!world.isClient() && user.isCreativeLevelTwoOp()) {
            var player = (ServerPlayerEntity) user;
            var taterPositionMap = stack.get(NEDataComponentTypes.TATER_POSITIONS);

            if (taterPositionMap != null) {
                if (user.isSneaking()) {
                    recordToGuidebook(player, HashMultimap.create(taterPositionMap.positions()), stack);
                } else {
                    showGuidebook(player, taterPositionMap.positions(), stack);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return Items.WRITTEN_BOOK;
    }

    @Override
    public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    private static void recordToGuidebook(ServerPlayerEntity player, SetMultimap<RegistryEntry<Item>, BlockPos> taterPositions, ItemStack stack) {
        int initialCount = taterPositions.size();

        var chunkManager = player.getServerWorld().getChunkManager();

        var chunkStorage = chunkManager.chunkLoadingManager;
        var accessor = (ServerChunkLoadingManagerAccessor) (Object) chunkStorage;

        for (var holder : accessor.callEntryIterator()) {
            var chunk = holder.getWorldChunk();

            if (chunk != null) {
                recordChunk(chunk, taterPositions);
            }
        }

        stack.set(NEDataComponentTypes.TATER_POSITIONS, new TaterPositionsComponent(taterPositions));

        player.getItemCooldownManager().set(stack, RECORD_COOLDOWN);

        int difference = taterPositions.size() - initialCount;
        player.sendMessage(Text.translatable("text.nucleoid_extras.tater_guidebook.recorded", difference), true);
    }

    private static void recordChunk(Chunk chunk, SetMultimap<RegistryEntry<Item>, BlockPos> taterPositions) {
        chunk.forEachBlockMatchingPredicate(state -> {
            return state.getBlock() instanceof TinyPotatoBlock;
        }, (pos, state) -> {
            taterPositions.put(state.getBlock().asItem().getRegistryEntry(), pos.toImmutable());
        });
    }

    private static void showGuidebook(ServerPlayerEntity player, SetMultimap<RegistryEntry<Item>, BlockPos> taterPositionMap, ItemStack stack) {
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

                var tater = taters.next().asItem().getRegistryEntry();
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

    private static HoverEvent getHoverEvent(RegistryEntry<Item> tater, Set<BlockPos> positions) {
        var hoverText = tater.value().getName().copy();

        for (var pos : positions) {
            hoverText.append(ScreenTexts.LINE_BREAK);

            Text coordinates = Text.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ());
            hoverText.append(Texts.bracketed(coordinates).formatted(Formatting.GREEN));
        }

        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
    }

    private static ClickEvent getClickEvent(RegistryEntry<Item> tater, Set<BlockPos> positions) {
        if (positions.isEmpty()) return null;

        var pos = positions.iterator().next();
        var command = "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
    }
}
