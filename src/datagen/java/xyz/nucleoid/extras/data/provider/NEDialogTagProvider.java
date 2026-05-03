package xyz.nucleoid.extras.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.tags.DialogTags;
import xyz.nucleoid.extras.dialog.NEDialogs;

import java.util.concurrent.CompletableFuture;

public class NEDialogTagProvider extends FabricTagProvider<Dialog> {
    public NEDialogTagProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(dataOutput, Registries.DIALOG, registries);
    }

    @Override
    protected void addTags(Provider lookup) {
        this.builder(DialogTags.PAUSE_SCREEN_ADDITIONS)
            .addOptional(NEDialogs.RULES);
    }
}
