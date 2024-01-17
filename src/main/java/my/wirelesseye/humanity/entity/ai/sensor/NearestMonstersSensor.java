package my.wirelesseye.humanity.entity.ai.sensor;

import my.wirelesseye.humanity.entity.ai.AllMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;

public class NearestMonstersSensor extends NearestVisibleLivingEntitySensor {
    @Override
    protected boolean matches(LivingEntity entity, LivingEntity target) {
        float distance = 10.0f;
        return target instanceof Monster
                && !(target instanceof Angerable)
                && target.squaredDistanceTo(entity) <= (double) (distance * distance);
    }

    @Override
    protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
        return AllMemoryModuleTypes.NEAREST_MONSTERS;
    }
}