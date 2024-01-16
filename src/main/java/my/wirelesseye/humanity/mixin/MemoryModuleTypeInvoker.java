package my.wirelesseye.humanity.mixin;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MemoryModuleType.class)
public interface MemoryModuleTypeInvoker {
    @Invoker("register")
    static <U> MemoryModuleType<U> register(String id) {
        throw new AssertionError();
    }
}
