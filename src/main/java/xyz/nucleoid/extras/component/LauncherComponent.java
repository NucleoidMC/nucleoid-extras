package xyz.nucleoid.extras.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;

public record LauncherComponent(float pitch, float power) implements PolymerComponent {
    public static final LauncherComponent DEFAULT = new LauncherComponent(10, 4);

    public static final Codec<LauncherComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.FLOAT.optionalFieldOf("pitch", DEFAULT.pitch).forGetter(LauncherComponent::pitch),
                Codec.FLOAT.optionalFieldOf("power", DEFAULT.power).forGetter(LauncherComponent::power)
        ).apply(instance, LauncherComponent::new)
    );
}
