package xyz.nucleoid.extras.lobby;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class NEPointOfInterestTypes {
    public static final RegistryKey<PointOfInterestType> TREE_DECORATION = of("tree_decoration");

    public static void register() {
        register(TREE_DECORATION, 0, 1, NEBlocks.TREE_DECORATION);
    }

    private static RegistryKey<PointOfInterestType> of(String id) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(id));
    }

    private static PointOfInterestType register(RegistryKey<PointOfInterestType> key, int ticketCount, int searchDistance, Block... blocks) {
        return PointOfInterestHelper.register(key.getValue(), ticketCount, searchDistance, blocks);
    }
}
