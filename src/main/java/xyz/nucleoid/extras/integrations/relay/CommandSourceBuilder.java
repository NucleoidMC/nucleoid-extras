package xyz.nucleoid.extras.integrations.relay;

import dev.gegy.roles.api.PlayerRolesApi;
import dev.gegy.roles.api.Role;
import dev.gegy.roles.api.RoleReader;
import dev.gegy.roles.api.VirtualServerCommandSource;
import dev.gegy.roles.api.override.RoleOverrideReader;
import dev.gegy.roles.api.override.RoleOverrideType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface CommandSourceBuilder {
    CommandSourceBuilder INSTANCE = FabricLoader.getInstance().isModLoaded("player_roles") ? new PlayerRoles() : new Vanilla();

    CommandSourceStack buildCommandSource(CommandSource output, MinecraftServer server, String name, int permissionLevel, List<String> roles);

    final class Vanilla implements CommandSourceBuilder {
        Vanilla() {
        }

        @Override
        public CommandSourceStack buildCommandSource(CommandSource output, MinecraftServer server, String name, int permissionLevel, List<String> roles) {
            return new CommandSourceStack(output, Vec3.ZERO, Vec2.ZERO, server.overworld(), permissionLevel, name, Component.literal(name), server, null);
        }
    }

    final class PlayerRoles implements CommandSourceBuilder {
        PlayerRoles() {
        }

        @Override
        public CommandSourceStack buildCommandSource(CommandSource output, MinecraftServer server, String name, int permissionLevel, List<String> roles) {
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
            return new VirtualServerCommandSource(roleReader, output, Vec3.ZERO, Vec2.ZERO, server.overworld(), permissionLevel, name, Component.literal(name), server, null);
        }
    }
}
