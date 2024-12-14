package xyz.nucleoid.extras.mixin.debug;

import io.netty.handler.timeout.TimeoutException;
import net.minecraft.network.ClientConnection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Redirect(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V"))
    private void printError(Logger instance, String s, Throwable throwable) {
        if (throwable instanceof TimeoutException) {
            return;
        }
        instance.error(s, throwable);
    }
}
