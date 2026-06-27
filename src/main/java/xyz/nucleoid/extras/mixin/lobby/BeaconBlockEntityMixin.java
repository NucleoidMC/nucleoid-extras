package xyz.nucleoid.extras.mixin.lobby;

import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.extras.lobby.block.ContainerLockAccess;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin implements ContainerLockAccess {
    @Shadow
    private LockCode lockKey;

    @Shadow
    public abstract Component getDisplayName();

    @Override
    public LockCode getContainerLock() {
        return this.lockKey;
    }

    @Override
    public void setContainerLock(LockCode lock) {
        this.lockKey = lock;
    }

    @Override
    public Component getContainerLockName() {
        return this.getDisplayName();
    }
}
