package xyz.nucleoid.extras.lobby.contributor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record ContributorSocials(UUID minecraft) {
    protected static final Codec<ContributorSocials> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                UUIDUtil.AUTHLIB_CODEC.fieldOf("minecraft").forGetter(ContributorSocials::minecraft)
        ).apply(instance, ContributorSocials::new)
    );
}
