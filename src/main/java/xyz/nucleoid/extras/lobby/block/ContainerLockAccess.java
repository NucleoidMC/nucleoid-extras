package xyz.nucleoid.extras.lobby.block;

import net.minecraft.inventory.ContainerLock;
import net.minecraft.text.Text;

public interface ContainerLockAccess {
    ContainerLock getContainerLock();

    void setContainerLock(ContainerLock lock);

    Text getContainerLockName();
}
