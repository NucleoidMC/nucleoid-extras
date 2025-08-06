package xyz.nucleoid.extras.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.api.util.PlasmidCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record RulesConfig(
    List<List<Text>> pages
) {
    private static final Codec<List<Text>> PAGE_CODEC = MoreCodecs.listOrUnit(PlasmidCodecs.TEXT);
    public static final Codec<RulesConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
        PAGE_CODEC.listOf().fieldOf("pages").forGetter(RulesConfig::pages)
    ).apply(i, RulesConfig::new));

    private static final Text DIALOG_TITLE = Text.translatable("text.nucleoid_extras.rules.title");
    private static final Text DIALOG_EXTERNAL_TITLE = Text.translatable("text.nucleoid_extras.rules.external_title");

    private static final int DIALOG_BODY_WIDTH = 300;

    public Dialog createDialog() {
        var body = new ArrayList<DialogBody>();

        for (List<Text> page : this.pages()) {
            Text combinedPage = Texts.join(page, ScreenTexts.LINE_BREAK);
            body.add(new PlainMessageDialogBody(combinedPage, DIALOG_BODY_WIDTH));
        }

        var data = new DialogCommonData(DIALOG_TITLE, Optional.of(DIALOG_EXTERNAL_TITLE), true, false, AfterAction.CLOSE, body, List.of());

        return new NoticeDialog(data, NoticeDialog.OK_BUTTON);
    }
}
