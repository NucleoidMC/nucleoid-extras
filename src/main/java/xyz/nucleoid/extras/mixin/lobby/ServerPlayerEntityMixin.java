package xyz.nucleoid.extras.mixin.lobby;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.extras.component.NEDataComponentTypes;
import xyz.nucleoid.extras.component.TaterSelectionComponent;
import xyz.nucleoid.extras.lobby.NECriteria;
import xyz.nucleoid.extras.lobby.PlayerLobbyState;
import xyz.nucleoid.extras.lobby.block.tater.CubicPotatoBlock;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    public ServerPlayerEntityMixin(Level world, GameProfile gameProfile) {
        super(world,  gameProfile);
    }

    @Inject(method = "doTick", at = @At("TAIL"))
    private void extras$playerTick(CallbackInfo ci) {
        ItemStack helmet = this.getItemBySlot(EquipmentSlot.HEAD);
        TaterSelectionComponent taterSelection = helmet.getOrDefault(NEDataComponentTypes.TATER_SELECTION, TaterSelectionComponent.DEFAULT);

        taterSelection.tater().ifPresent(tater -> {
            if (tater.value() instanceof CubicPotatoBlock tinyPotatoBlock) {
                ServerPlayer player = (ServerPlayer) (Object) this;
                PlayerLobbyState state = PlayerLobbyState.get(player);

                if (state.collectedTaters.contains(tinyPotatoBlock)) {
                    NECriteria.WEAR_TATER.trigger(player, tinyPotatoBlock);
                    NECriteria.TATER_COLLECTED.trigger(player, tinyPotatoBlock, state.collectedTaters.size());
                    if (this.tickCount % tinyPotatoBlock.getPlayerParticleRate(player) == 0) {
                        tinyPotatoBlock.spawnPlayerParticles(player);
                    }
                } else {
                    helmet.set(NEDataComponentTypes.TATER_SELECTION, taterSelection.selected(null));
                }
            }
        });
    }
}
