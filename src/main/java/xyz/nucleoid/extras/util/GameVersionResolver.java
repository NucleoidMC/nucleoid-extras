package xyz.nucleoid.extras.util;

import org.jetbrains.annotations.Nullable;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

public final class GameVersionResolver {
    private static final boolean VIAVERSION = FabricLoader.getInstance().isModLoaded("viaversion");

    private GameVersionResolver() {
        return;
    }

    public static String getVersion() {
        if (VIAVERSION) {
            String version = getViaVersionHighestSupportedVersion();
            if (version != null) {
                return version;
            }
        }

        return SharedConstants.getGameVersion().getName();
    }

    /**
     * {@return the name of the highest supported version by ViaVersion, or {@code null} if it could not be resolved}
     */
    @Nullable
    private static String getViaVersionHighestSupportedVersion() {
        try {
            int maxVersion = (int) Via.getAPI().getSupportedVersions().last();
            ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(maxVersion);

            return protocolVersion.getName();
        } catch (IllegalArgumentException e) {
            // The ViaVersion API does not provide a way
            // to test whether the platform is loaded,
            // so the exception must be caught instead.
            return null;
        }
    }
}
