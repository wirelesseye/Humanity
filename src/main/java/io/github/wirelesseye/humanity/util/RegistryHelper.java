package io.github.wirelesseye.humanity.util;

import io.github.wirelesseye.humanity.mixin.DefaultAttributeRegistryAccessor;
import io.github.wirelesseye.humanity.mixin.EntityTypeInvolker;
import io.github.wirelesseye.humanity.mixin.MemoryModuleTypeInvoker;
import io.github.wirelesseye.humanity.mixin.SensorTypeInvoker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class RegistryHelper {
    public static <U extends Sensor<?>>SensorType<U> registerSensorType(String id, Supplier<U> factory) {
        return SensorTypeInvoker.register(id, factory);
    }

    public static <U> MemoryModuleType<U> registerMemoryModuleType(String id) {
        return MemoryModuleTypeInvoker.register(id);
    }

    public static <T extends Entity> EntityType<T> registerEntityType(Identifier id, EntityType.Builder<T> type) {
        return EntityTypeInvolker.register(id.toString(), type);
    }

    public static void registerDefaultAttributes(EntityType<? extends LivingEntity> type, DefaultAttributeContainer.Builder builder) {
        DefaultAttributeRegistryAccessor.getRegistry().put(type, builder.build());
    }
}
