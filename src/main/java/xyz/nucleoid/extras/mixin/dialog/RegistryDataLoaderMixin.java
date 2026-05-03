package xyz.nucleoid.extras.mixin.dialog;

import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.server.dialog.Dialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.extras.dialog.NEDialogs;

import java.util.Map;
import java.util.stream.Stream;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "lambda$load$4", at = @At("HEAD"))
    private static void registerNucleoidExtrasDialogs(Map loadingErrors, RegistryLoadTask task, CallbackInfoReturnable<Stream> cir) {
        if (task.createRegistryInfo().owner() instanceof WritableRegistry<?> registry && registry.key() == Registries.DIALOG) {
            NEDialogs.register((WritableRegistry<Dialog>) registry);
        }
    }
}
