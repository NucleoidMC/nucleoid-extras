package xyz.nucleoid.extras.mixin;

import com.mojang.brigadier.ResultConsumer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerCommandSource.class)
public interface ServerCommandSourceAccessor {
    @Invoker("<init>")
    static ServerCommandSource create(
            CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level,
            String simpleName, Text name, MinecraftServer server,
            @Nullable Entity entity, boolean silent,
            ResultConsumer<ServerCommandSource> consumer, EntityAnchorArgumentType.EntityAnchor entityAnchor
    ) {
        throw new UnsupportedOperationException();
    }
}
