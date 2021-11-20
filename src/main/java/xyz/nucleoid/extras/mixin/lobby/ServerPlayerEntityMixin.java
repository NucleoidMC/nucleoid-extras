package xyz.nucleoid.extras.mixin.lobby;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.block.TinyPotatoBlock;
import xyz.nucleoid.extras.lobby.item.TaterBoxItem;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "playerTick", at = @At("TAIL"))
    private void playerTick(CallbackInfo ci) {
        if (this.age % 2 == 0) {
            ItemStack helmet = this.getEquippedStack(EquipmentSlot.HEAD);
            if (helmet.getItem() instanceof TaterBoxItem) {
                if (TaterBoxItem.getSelectedTater(helmet) instanceof TinyPotatoBlock tinyPotatoBlock) {
                    tinyPotatoBlock.spawnPlayerParticles((ServerPlayerEntity) (Object) this);
                }
            }
        }
    }
}
