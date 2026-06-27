package xyz.nucleoid.extras.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.PlainMessage;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RulesConfig(
    List<List<Component>> pages
) {
    private static final Codec<List<Component>> PAGE_CODEC = MoreCodecs.listOrUnit(PlasmidCodecs.TEXT);
    public static final Codec<RulesConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
        PAGE_CODEC.listOf().fieldOf("pages").forGetter(RulesConfig::pages)
    ).apply(i, RulesConfig::new));

    private static final Component DIALOG_TITLE = Component.translatable("text.nucleoid_extras.rules.title");
    private static final Component DIALOG_EXTERNAL_TITLE = Component.translatable("text.nucleoid_extras.rules.external_title");

    private static final int DIALOG_BODY_WIDTH = 300;

    public Dialog createDialog() {
        var body = new ArrayList<DialogBody>();

        for (List<Component> page : this.pages()) {
            Component combinedPage = ComponentUtils.formatList(page, CommonComponents.NEW_LINE);
            body.add(new PlainMessage(combinedPage, DIALOG_BODY_WIDTH));
        }

        var data = new CommonDialogData(DIALOG_TITLE, Optional.of(DIALOG_EXTERNAL_TITLE), true, false, DialogAction.CLOSE, body, List.of());

        return new NoticeDialog(data, NoticeDialog.DEFAULT_ACTION);
    }
}
