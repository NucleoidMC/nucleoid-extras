package xyz.nucleoid.extras.mixin.dialog;

import com.mojang.serialization.Decoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.resource.ResourceManager;
import xyz.nucleoid.extras.dialog.NEDialogs;

import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At("HEAD"))
    private static void registerNucleoidExtrasDialogs(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry<?> registry, Decoder<?> elementDecoder, Map<RegistryKey<?>, Exception> errors, CallbackInfo ci) {
        if (registry.getKey() == RegistryKeys.DIALOG) {
            NEDialogs.register((MutableRegistry<Dialog>) registry);
        }
    }
}
