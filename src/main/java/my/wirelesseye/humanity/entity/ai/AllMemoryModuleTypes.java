package my.wirelesseye.humanity.entity.ai;

import my.wirelesseye.humanity.util.RegistryHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class AllMemoryModuleTypes {
    public static final MemoryModuleType<LivingEntity> NEAREST_MONSTERS =
            RegistryHelper.registerMemoryModuleType("nearest_monsters");
}
