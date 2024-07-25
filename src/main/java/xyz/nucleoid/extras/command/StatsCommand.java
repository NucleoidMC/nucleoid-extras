package xyz.nucleoid.extras.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import xyz.nucleoid.extras.integrations.game.StatisticsIntegration;
import xyz.nucleoid.extras.integrations.http.NucleoidHttpClient;
import xyz.nucleoid.extras.util.CommonGuiElements;
import xyz.nucleoid.extras.util.PagedGui;
import xyz.nucleoid.plasmid.game.stats.GameStatisticBundle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class StatsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("stats").executes(StatsCommand::openScreen));
    }

    private static int openScreen(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {


        var base = Text.translatable("text.nucleoid_extras.statistics.waiting").append("   ");

        var gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, context.getSource().getPlayerOrThrow(), false) {
            int tick = 0;

            @Override
            public void onTick() {
                this.setTitle(base.copy().append(".".repeat((this.tick / 20) % 4)));
                tick++;
            }
        };

        while (gui.getFirstEmptySlot() != -1) {
            gui.addSlot(CommonGuiElements.white());
        }

        gui.setTitle(base.copy());

        gui.open();

        NucleoidHttpClient.getPlayerStats(context.getSource().getPlayerOrThrow().getUuid()).thenAcceptAsync(data -> {
            openMain(data, context.getSource().getPlayer());
        }, context.getSource().getServer());

        return 0;
    }

    private static void openMain(Map<String, Map<Identifier, Number>> stats, ServerPlayerEntity player) {
        var list = new ArrayList<GuiElementInterface>();
        for (var entry : stats.entrySet()) {
            var builder = new GuiElementBuilder(Items.PAPER);
            builder.setName(Text.translatable(GameStatisticBundle.getTranslationKey(entry.getKey())));
            builder.setCallback((a, b, c, d) -> {
                PagedGui.playClickSound(player);
                openTargetStats(entry.getKey(), entry.getValue(), player);
            });
            list.add(builder.build());
        }
        list.sort(Comparator.comparing(x -> x.getItemStack().getName().getString()));
        var gui = PagedGui.of(player, list, null);
        gui.setTitle(Text.translatable("text.nucleoid_extras.statistics.list_ui"));
        gui.open();
    }

    private static void openTargetStats(String key, Map<Identifier, Number> value, ServerPlayerEntity player) {
        var cGui = GuiHelpers.getCurrentGui(player);
        var list = new ArrayList<GuiElementInterface>();
        for (var entry : value.entrySet()) {
            var builder = new GuiElementBuilder(Items.NAME_TAG);
            builder.setName(Text.empty().append(Util.createTranslationKey("statistic", entry.getKey())).append(": ").append(StatisticsIntegration.convertForDisplay(entry.getKey(), entry.getValue())));
            list.add(builder.build());
        }
        list.sort(Comparator.comparing(x -> x.getItemStack().getName().getString()));
        var g = PagedGui.of(player, list, (id) -> id == 8 ? CommonGuiElements.back(cGui::open).build() : null);
        g.setTitle(Text.translatable(GameStatisticBundle.getTranslationKey(key)));
        g.open();
    }
}
