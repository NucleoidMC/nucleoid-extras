package xyz.nucleoid.extras.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import xyz.nucleoid.extras.data.provider.NEAdvancementProvider;
import xyz.nucleoid.extras.data.provider.NEBlockTagProvider;
import xyz.nucleoid.extras.data.provider.NEItemTagProvider;
import xyz.nucleoid.extras.data.provider.NEModelProvider;

public class NEDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        var pack = dataGenerator.createPack();

        pack.addProvider(NEAdvancementProvider::new);

        var blockTags = pack.addProvider(NEBlockTagProvider::new);
        pack.addProvider((dataOutput, registries) -> new NEItemTagProvider(dataOutput, registries, blockTags));
        pack.addProvider(NEModelProvider::new);
    }
}
