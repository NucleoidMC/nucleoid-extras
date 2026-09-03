package xyz.nucleoid.extras.lobby.contributor;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Uuids;

public record ContributorSocials(UUID minecraft) {
    protected static final Codec<ContributorSocials> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Uuids.CODEC.fieldOf("minecraft").forGetter(ContributorSocials::minecraft)
        ).apply(instance, ContributorSocials::new)
    );
}
