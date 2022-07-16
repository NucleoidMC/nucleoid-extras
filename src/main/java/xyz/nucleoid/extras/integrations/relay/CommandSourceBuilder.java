package xyz.nucleoid.extras.integrations.relay;

import dev.gegy.roles.api.PlayerRolesApi;
import dev.gegy.roles.api.Role;
import dev.gegy.roles.api.RoleReader;
import dev.gegy.roles.api.VirtualServerCommandSource;
import dev.gegy.roles.api.override.RoleOverrideReader;
import dev.gegy.roles.api.override.RoleOverrideType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface CommandSourceBuilder {
    CommandSourceBuilder INSTANCE = FabricLoader.getInstance().isModLoaded("player_roles") ? new PlayerRoles() : new Vanilla();

    ServerCommandSource buildCommandSource(CommandOutput output, MinecraftServer server, String name, int permissionLevel, List<String> roles);

    final class Vanilla implements CommandSourceBuilder {
        Vanilla() {
        }

        @Override
        public ServerCommandSource buildCommandSource(CommandOutput output, MinecraftServer server, String name, int permissionLevel, List<String> roles) {
            return new ServerCommandSource(output, Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), permissionLevel, name, new LiteralText(name), server, null);
        }
    }

    final class PlayerRoles implements CommandSourceBuilder {
        PlayerRoles() {
        }

        @Override
        public ServerCommandSource buildCommandSource(CommandOutput output, MinecraftServer server, String name, int permissionLevel, List<String> roles) {
            var resolvedRoles = new ArrayList<Role>();
            for (var roleId : roles) {
                var role = PlayerRolesApi.provider().get(roleId);
                if (role != null) {
                    resolvedRoles.add(role);
                }
            }

            resolvedRoles.sort(null);

            var overrides = new Reference2ObjectOpenHashMap<RoleOverrideType<?>, List<Object>>();
            for (var role : resolvedRoles) {
                for (var type : role.getOverrides().typeSet()) {
                    overrides.computeIfAbsent(type, __ -> new ArrayList<>()).addAll(role.getOverrides().get(type));
                }
            }

            var overrideReader = new RoleOverrideReader() {
                @Override
                @SuppressWarnings("unchecked")
                public @Nullable <T> Collection<T> getOrNull(RoleOverrideType<T> type) {
                    return (List<T>) overrides.get(type);
                }

                @Override
                public Set<RoleOverrideType<?>> typeSet() {
                    return overrides.keySet();
                }
            };

            var roleReader = new RoleReader() {
                @NotNull
                @Override
                public Iterator<Role> iterator() {
                    return resolvedRoles.iterator();
                }

                @Override
                public boolean has(Role role) {
                    return resolvedRoles.contains(role);
                }

                @Override
                public RoleOverrideReader overrides() {
                    return overrideReader;
                }
            };
            return new VirtualServerCommandSource(roleReader, output, Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), permissionLevel, name, new LiteralText(name), server, null);
        }
    }
}
