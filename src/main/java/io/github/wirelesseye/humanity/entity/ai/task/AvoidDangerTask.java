package io.github.wirelesseye.humanity.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import io.github.wirelesseye.humanity.mixin.CreeperEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;


public class AvoidDangerTask extends Task<HumanEntity> {
    public AvoidDangerTask() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT,
                MemoryModuleType.AVOID_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED));
    }

    @Override
    protected void run(ServerWorld world, HumanEntity entity, long time) {
        Brain<HumanEntity> brain = entity.getBrain();
        LivingEntity target = brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (isDanger(entity, target)) {
            brain.remember(MemoryModuleType.AVOID_TARGET, target);
            brain.forget(MemoryModuleType.WALK_TARGET);
        }
    }

    private boolean isDanger(HumanEntity entity, LivingEntity target) {
        return target instanceof CreeperEntity creeper
                && ((CreeperEntityAccessor) creeper).getCurrentFuseTime() > 5;
    }
}
