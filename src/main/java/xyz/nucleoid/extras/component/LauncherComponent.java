package xyz.nucleoid.extras.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

import java.util.Optional;

public record LauncherComponent(float pitch, float power, Optional<RegistryEntry<SoundEvent>> sound) implements PolymerComponent {
    public static final LauncherComponent DEFAULT = new LauncherComponent(10, 4, Optional.empty());

    public static final Codec<LauncherComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.FLOAT.optionalFieldOf("pitch", DEFAULT.pitch).forGetter(LauncherComponent::pitch),
                Codec.FLOAT.optionalFieldOf("power", DEFAULT.power).forGetter(LauncherComponent::power),
                SoundEvent.ENTRY_CODEC.optionalFieldOf("sound").forGetter(LauncherComponent::sound)
        ).apply(instance, LauncherComponent::new)
    );
}
