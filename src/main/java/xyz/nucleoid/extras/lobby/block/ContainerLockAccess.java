package xyz.nucleoid.extras.lobby.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.LockCode;

public interface ContainerLockAccess {
    LockCode getContainerLock();

    void setContainerLock(LockCode lock);

    Component getContainerLockName();
}
