package xyz.nucleoid.extras.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.impl.portal.GamePortal;
import xyz.nucleoid.plasmid.impl.portal.GamePortalManager;

public record GamePortalComponent(Identifier gamePortalId) implements PolymerComponent {
    public static final Codec<GamePortalComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Identifier.CODEC.fieldOf("game_portal_id").forGetter(GamePortalComponent::gamePortalId)
        ).apply(instance, GamePortalComponent::new)
    );

    public GamePortal getGamePortal() {
        return GamePortalManager.INSTANCE.byId(this.gamePortalId);
    }
}
