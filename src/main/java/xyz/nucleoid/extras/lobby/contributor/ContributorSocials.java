package xyz.nucleoid.extras.lobby.contributor;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ContributorSocials(UUID minecraft) {
    protected static final Codec<ContributorSocials> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                UUIDUtil.AUTHLIB_CODEC.fieldOf("minecraft").forGetter(ContributorSocials::minecraft)
        ).apply(instance, ContributorSocials::new)
    );
}
