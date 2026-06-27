package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.contributor.Contributor;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.util.PagedGui;
import xyz.nucleoid.plasmid.api.util.PlayerUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ContributorStatueBlockEntity extends BlockEntity {
    protected static final String CONTRIBUTOR_ID_KEY = "contributor_id";

    private static final Component GUI_TITLE = Component.translatable("text.nucleoid_extras.contributor_statue.title");

    private String contributorId = "";

    private ContributorStatueModel model;

    public ContributorStatueBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.CONTRIBUTOR_STATUE_ENTITY, pos, state);
    }

    public void attachElementHolder(LevelChunk chunk) {
        var attachment = BlockAwareAttachment.get(chunk, this.getBlockPos());

        if (attachment != null && attachment.holder() instanceof ContributorStatueModel model) {
            this.model = model;
            this.updateModel();
        } else {
            this.model = null;
        }
    }

    public void updateModel() {
        if (this.model != null) {
            this.model.update(this.contributorId, (ServerLevel) this.level, this.getBlockState());
        }
    }

    private void selectContributor(ServerPlayer player, String id) {
        if (this.contributorId.equals(id)) return;

        this.contributorId = id;
        PlayerUtil.playSoundToPlayer(player, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.UI, 1, 1);

        this.updateModel();
        this.setChanged();
    }

    protected void openEditScreen(ServerPlayer player) {
        var server = player.level().getServer();

        List<GuiElement> elements = ContributorData.getContributors()
                .stream()
                .sorted((a, b) -> {
                    return a.getValue().compareTo(b.getValue());
                })
                .map(entry -> {
                    var id = entry.getKey();
                    var contributor = entry.getValue();

                    var profile = contributor.createGameProfile(server);

                    var builder = GuiElementBuilder.from(contributor.createPlayerHead(profile))
                        .setItemName(contributor.getName())
                        .hideDefaultTooltip()
                        .setCallback(() -> {
                            this.selectContributor(player, id);
                        });

                    var element = builder.build();

                    contributor.loadGameProfileProperties(server, profile, fullProfile -> {
                        Contributor.writeSkullOwner(element.getItemStack(), fullProfile);
                    });

                    return element;
                })
                .collect(Collectors.toList());

        elements.add(0, new GuiElementBuilder(Items.BARRIER)
            .setItemName(TaterBoxItem.NONE_TEXT)
            .hideDefaultTooltip()
            .setCallback(() -> {
                this.selectContributor(player, "");
            })
            .build());

        var gui = PagedGui.of(player, elements);
        gui.setTitle(GUI_TITLE);

        gui.open();
    }

    @Override
    public void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.contributorId = view.getStringOr(CONTRIBUTOR_ID_KEY, "");
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putString(CONTRIBUTOR_ID_KEY, this.contributorId);
    }
}
