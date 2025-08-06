package xyz.nucleoid.extras.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.nucleoid.extras.data.provider.*;

public class NEDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        var pack = dataGenerator.createPack();

        // Assets
        pack.addProvider(NEAssetProvider::new);
        pack.addProvider(NEModelProvider::new);

        // Data
        pack.addProvider(NEAdvancementProvider::new);
        var blockTags = pack.addProvider(NEBlockTagProvider::new);
        pack.addProvider((dataOutput, registries) -> new NEItemTagProvider(dataOutput, registries, blockTags));
        pack.addProvider(NEDialogTagProvider::new);
    }
}
