package xyz.nucleoid.extras.mixin.lobby;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.extras.lobby.block.ContainerLockAccess;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin implements ContainerLockAccess {
    @Shadow
    private ContainerLock lock;

    @Shadow
    public abstract Text getDisplayName();

    @Override
    public ContainerLock getContainerLock() {
        return this.lock;
    }

    @Override
    public void setContainerLock(ContainerLock lock) {
        this.lock = lock;
    }

    @Override
    public Text getContainerLockName() {
        return this.getDisplayName();
    }
}
