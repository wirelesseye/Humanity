package io.github.wirelesseye.humanity.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityType.class)
public interface EntityTypeInvolker {
    @Invoker("register")
    static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        throw new AssertionError();
    }
}
