package xyz.nucleoid.extras.mixin.lobby;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import xyz.nucleoid.extras.lobby.block.ContributorStatueBlockEntity;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Inject(method = "updateGameEventListener", at = @At("TAIL"))
    private void callMethod(BlockEntity blockEntity, ServerWorld world, CallbackInfo ci) {
        if (blockEntity instanceof ContributorStatueBlockEntity contributorStatue) {
            contributorStatue.attachElementHolder((WorldChunk) (Object) this);
        }
    }
}
