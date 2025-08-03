package xyz.nucleoid.extras.data.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.DialogTags;
import xyz.nucleoid.extras.dialog.NEDialogs;

import java.util.concurrent.CompletableFuture;

public class NEDialogTagProvider extends FabricTagProvider<Dialog> {
    public NEDialogTagProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(dataOutput, RegistryKeys.DIALOG, registries);
    }

    @Override
    protected void configure(WrapperLookup lookup) {
        this.builder(DialogTags.PAUSE_SCREEN_ADDITIONS)
            .addOptional(NEDialogs.RULES);
    }
}
