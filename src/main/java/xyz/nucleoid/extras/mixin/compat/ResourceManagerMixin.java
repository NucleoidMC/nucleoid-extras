package xyz.nucleoid.extras.mixin.compat;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(ResourceManager.class)
public interface ResourceManagerMixin {
    @Shadow
    Map<Identifier, Resource> listResources(String directory, ResourceManager.Selector selector);

    default Map<Identifier, Resource> listResources(String directory, Predicate<Identifier> filter) {
        return this.listResources(directory, new ResourceManager.Selector() {
            @Override
            public boolean isIncluded(Identifier resourceId) {
                return filter.test(resourceId);
            }
        });
    }
}
