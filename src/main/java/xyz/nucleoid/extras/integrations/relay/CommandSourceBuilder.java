package xyz.nucleoid.extras.integrations.relay;

import dev.gegy.roles.api.PlayerRolesApi;
import dev.gegy.roles.api.Role;
import dev.gegy.roles.api.RoleReader;
import dev.gegy.roles.api.VirtualServerCommandSource;
import dev.gegy.roles.api.override.RoleOverrideReader;
import dev.gegy.roles.override.RoleOverrideMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            var overrideMap = new RoleOverrideMap();
            for (var roleId : roles) {
                var role = PlayerRolesApi.provider().get(roleId);
                if (role != null) {
                    resolvedRoles.add(role);
                }
            }
            resolvedRoles.sort(null);
            for (var role : resolvedRoles) {
                overrideMap.addAll(role.getOverrides());
            }
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
                    return overrideMap;
                }
            };
            return new VirtualServerCommandSource(roleReader, output, Vec3d.ZERO, Vec2f.ZERO, server.getOverworld(), permissionLevel, name, new LiteralText(name), server, null);
        }
    }
}
