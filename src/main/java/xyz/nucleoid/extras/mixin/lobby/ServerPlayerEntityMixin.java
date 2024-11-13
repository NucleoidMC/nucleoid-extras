package xyz.nucleoid.extras.mixin.lobby;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterSelectionComponent;
import xyz.nucleoid.extras.lobby.NECriteria;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;
import xyz.nucleoid.extras.lobby.particle.TaterParticleContext;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "playerTick", at = @At("TAIL"))
    private void extras$playerTick(CallbackInfo ci) {
        ItemStack helmet = this.getEquippedStack(EquipmentSlot.HEAD);
        TaterSelectionComponent taterSelection = helmet.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT);

        if (taterSelection.tater().isPresent() && taterSelection.tater().get().value() instanceof CubicPotatoBlock tinyPotatoBlock) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            NECriteria.WEAR_TATER.trigger(player, tinyPotatoBlock);
            NECriteria.TATER_COLLECTED.trigger(player, tinyPotatoBlock, PlayerLobbyState.get(this).collectedTaters.size());
            tinyPotatoBlock.getParticleSpawner().trySpawn(new TaterParticleContext.Player(player));
        }
    }
}
