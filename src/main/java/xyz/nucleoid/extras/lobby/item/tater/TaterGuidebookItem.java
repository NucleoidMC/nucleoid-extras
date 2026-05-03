package xyz.nucleoid.extras.lobby.item.tater;

import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.sgui.api.elements.BookElementBuilder;
import eu.pb4.sgui.api.gui.BookGui;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterPositionsComponent;
import xyz.nucleoid.extras.lobby.block.tater.TinyPotatoBlock;
import xyz.nucleoid.packettweaker.PacketContext;

public class TaterGuidebookItem extends Item implements PolymerItem {
    private static final Component MISSING_SYMBOL = Component.literal("❌").withStyle(ChatFormatting.RED);
    private static final Component FOUND_SYMBOL = Component.literal("✔").withStyle(ChatFormatting.GREEN);
    private static final Component TOO_MANY_SYMBOL = Component.literal("✔").setStyle(Style.EMPTY.withColor(0x055005));

    private static final int RECORD_COOLDOWN = 2 * SharedConstants.TICKS_PER_SECOND;

    public TaterGuidebookItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        var stack = user.getItemInHand(hand);

        if (!world.isClientSide() && user.canUseGameMasterBlocks()) {
            var player = (ServerPlayer) user;
            var taterPositionMap = stack.get(NEDataComponentTypes.TATER_POSITIONS);

            if (taterPositionMap != null) {
                if (user.isShiftKeyDown()) {
                    recordToGuidebook(player, HashMultimap.create(taterPositionMap.positions()), stack);
                } else {
                    showGuidebook(player, taterPositionMap.positions(), stack);
                }

                return InteractionResult.SUCCESS_SERVER;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        return Items.WRITTEN_BOOK;
    }

    @Override
    public ResourceLocation getPolymerItemModel(ItemStack stack, PacketContext context) {
        return null;
    }

    private static void recordToGuidebook(ServerPlayer player, SetMultimap<Holder<Item>, BlockPos> taterPositions, ItemStack stack) {
        int initialCount = taterPositions.size();

        var chunkManager = player.level().getChunkSource();


        chunkManager.chunkMap.forEachReadyToSendChunk(chunk -> recordChunk(chunk, taterPositions));

        stack.set(NEDataComponentTypes.TATER_POSITIONS, new TaterPositionsComponent(taterPositions));

        player.getCooldowns().addCooldown(stack, RECORD_COOLDOWN);

        int difference = taterPositions.size() - initialCount;
        player.displayClientMessage(Component.translatable("text.nucleoid_extras.tater_guidebook.recorded", difference), true);
    }

    private static void recordChunk(ChunkAccess chunk, SetMultimap<Holder<Item>, BlockPos> taterPositions) {
        chunk.findBlocks(state -> {
            return state.getBlock() instanceof TinyPotatoBlock;
        }, (pos, state) -> {
            taterPositions.put(state.getBlock().asItem().builtInRegistryHolder(), pos.immutable());
        });
    }

    private static void showGuidebook(ServerPlayer player, SetMultimap<Holder<Item>, BlockPos> taterPositionMap, ItemStack stack) {
        var builder = new BookElementBuilder();
        var taters = TaterBoxItem.getSortedTaterStream(player).iterator();

        boolean firstPage = true;

        while (taters.hasNext()) {
            var page = Component.empty();

            if (firstPage) {
                page.append(stack.getHoverName().copy().withStyle(ChatFormatting.BOLD));
                page.append(CommonComponents.NEW_LINE);

                page.append(Component.translatable("text.nucleoid_extras.tater_guidebook.header", taterPositionMap.size()));
                page.append(CommonComponents.NEW_LINE);
                page.append(CommonComponents.NEW_LINE);

                firstPage = false;
            }

            for (int index = 0; index < (16 * 4); index++) {
                if (!taters.hasNext()) break;

                var tater = taters.next().asItem().builtInRegistryHolder();
                var positions = taterPositionMap.get(tater);

                var symbol = getSymbol(positions);

                var hoverEvent = getHoverEvent(tater, positions);
                var clickEvent = getClickEvent(tater, positions);

                var text = symbol.copy().withStyle(style -> {
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

    private static Component getSymbol(Set<BlockPos> positions) {
        return switch (positions.size()) {
            case 0 -> MISSING_SYMBOL;
            case 1 -> FOUND_SYMBOL;
            default -> TOO_MANY_SYMBOL;
        };
    }

    private static HoverEvent getHoverEvent(Holder<Item> tater, Set<BlockPos> positions) {
        var hoverText = tater.value().getName().copy();

        for (var pos : positions) {
            hoverText.append(CommonComponents.NEW_LINE);

            Component coordinates = Component.translatable("chat.coordinates", pos.getX(), pos.getY(), pos.getZ());
            hoverText.append(ComponentUtils.wrapInSquareBrackets(coordinates).withStyle(ChatFormatting.GREEN));
        }

        return new HoverEvent.ShowText(hoverText);
    }

    private static ClickEvent getClickEvent(Holder<Item> tater, Set<BlockPos> positions) {
        if (positions.isEmpty()) return null;

        var pos = positions.iterator().next();
        var command = "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        return new ClickEvent.RunCommand(command);
    }
}
