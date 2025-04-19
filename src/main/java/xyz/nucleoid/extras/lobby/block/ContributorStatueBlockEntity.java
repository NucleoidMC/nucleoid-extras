package xyz.nucleoid.extras.lobby.block;

import java.util.List;
import java.util.stream.Collectors;

import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.lobby.contributor.Contributor;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;
import xyz.nucleoid.extras.util.PagedGui;

public class ContributorStatueBlockEntity extends BlockEntity {
    protected static final String CONTRIBUTOR_ID_KEY = "contributor_id";

    private static final Text GUI_TITLE = Text.translatable("text.nucleoid_extras.contributor_statue.title");

    private String contributorId = "";

    private ContributorStatueModel model;

    public ContributorStatueBlockEntity(BlockPos pos, BlockState state) {
        super(NEBlocks.CONTRIBUTOR_STATUE_ENTITY, pos, state);
    }

    public void attachElementHolder(WorldChunk chunk) {
        var attachment = BlockAwareAttachment.get(chunk, this.getPos());

        if (attachment != null && attachment.holder() instanceof ContributorStatueModel model) {
            this.model = model;
            this.updateModel();
        } else {
            this.model = null;
        }
    }

    public void updateModel() {
        if (this.model != null) {
            this.model.update(this.contributorId, (ServerWorld) this.world, this.getCachedState());
        }
    }

    private void selectContributor(ServerPlayerEntity player, String id) {
        if (this.contributorId.equals(id)) return;

        this.contributorId = id;
        player.playSoundToPlayer(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 1, 1);

        this.updateModel();
        this.markDirty();
    }

    protected void openEditScreen(ServerPlayerEntity player) {
        var server = player.getServer();

        List<GuiElementInterface> elements = ContributorData.getContributors()
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
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.contributorId = nbt.getString(CONTRIBUTOR_ID_KEY, "");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putString(CONTRIBUTOR_ID_KEY, this.contributorId);
    }
}
