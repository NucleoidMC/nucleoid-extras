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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.extras.ServerCommandSourceExt;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements ServerCommandSourceExt {
    @Shadow @Final private Vec3d position;
    @Shadow @Final private Vec2f rotation;
    @Shadow @Final private ServerWorld world;
    @Shadow @Final private int level;
    @Shadow @Final private String simpleName;
    @Shadow @Final private Text name;
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final @Nullable private Entity entity;
    @Shadow @Final private boolean silent;
    @Shadow @Final private ResultConsumer<ServerCommandSource> resultConsumer;
    @Shadow @Final private EntityAnchorArgumentType.EntityAnchor entityAnchor;

    @Override
    public ServerCommandSource withOutput(CommandOutput output) {
        return ServerCommandSourceAccessor.create(
                output, this.position, this.rotation, this.world, this.level,
                this.simpleName, this.name, this.server,
                this.entity, this.silent,
                this.resultConsumer, this.entityAnchor
        );
    }
}
