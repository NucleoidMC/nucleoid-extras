package xyz.nucleoid.extras.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.nucleoid.extras.data.provider.NEBlockTagProvider;
import xyz.nucleoid.extras.data.provider.NEItemTagProvider;

public class NEDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        var pack = dataGenerator.createPack();

        var blockTags = pack.addProvider(NEBlockTagProvider::new);
        pack.addProvider((dataOutput, registries) -> new NEItemTagProvider(dataOutput, registries, blockTags));
    }
}
