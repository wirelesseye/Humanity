package io.github.wirelesseye.humanity.util;

import io.github.wirelesseye.humanity.mixin.MemoryModuleTypeInvoker;
import io.github.wirelesseye.humanity.mixin.SensorTypeInvoker;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;

import java.util.function.Supplier;

public class RegistryHelper {
    public static <U extends Sensor<?>>SensorType<U> registerSensorType(String id, Supplier<U> factory) {
        return SensorTypeInvoker.register(id, factory);
    }

    public static <U> MemoryModuleType<U> registerMemoryModuleType(String id) {
        return MemoryModuleTypeInvoker.register(id);
    }
}
