package xyz.nucleoid.extras.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.nucleoid.extras.data.provider.*;

public class NEDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        var pack = dataGenerator.createPack();

        pack.addProvider(NEAdvancementProvider::new);
        pack.addProvider(NEAssetProvider::new);
        pack.addProvider(NEDialogTagProvider::new);

        var blockTags = pack.addProvider(NEBlockTagProvider::new);
        pack.addProvider((dataOutput, registries) -> new NEItemTagProvider(dataOutput, registries, blockTags));
    }
}
