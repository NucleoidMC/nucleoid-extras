package xyz.nucleoid.extras.mixin.relay;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TranslatableText.class)
public interface TranslatableTextInvoker {
	@Invoker
	StringVisitable invokeGetArg(int index);
}
