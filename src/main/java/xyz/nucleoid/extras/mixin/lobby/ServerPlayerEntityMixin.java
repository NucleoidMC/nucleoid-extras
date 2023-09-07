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
import xyz.nucleoid.extras.lobby.NECriteria;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;
import xyz.nucleoid.extras.lobby.item.tater.TaterBoxItem;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "playerTick", at = @At("TAIL"))
    private void extras$playerTick(CallbackInfo ci) {
        ItemStack helmet = this.getEquippedStack(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof TaterBoxItem) {
            if (TaterBoxItem.getSelectedTater(helmet) instanceof CubicPotatoBlock tinyPotatoBlock) {
                ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
                NECriteria.WEAR_TATER.trigger(player, TaterBoxItem.getSelectedTaterId(helmet));
                NECriteria.TATER_COLLECTED.trigger(player, TaterBoxItem.getSelectedTaterId(helmet), PlayerLobbyState.get(this).collectedTaters.size());
                if (this.age % tinyPotatoBlock.getPlayerParticleRate(player) == 0) {
                    tinyPotatoBlock.spawnPlayerParticles(player);
                }
            }
        }
    }
}
