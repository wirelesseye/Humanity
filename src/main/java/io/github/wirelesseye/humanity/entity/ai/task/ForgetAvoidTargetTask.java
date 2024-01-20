package io.github.wirelesseye.humanity.entity.ai.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;

import java.util.Optional;

public class ForgetAvoidTargetTask<E extends MobEntity> extends Task<E> {
    private final int range;

    public ForgetAvoidTargetTask(int range) {
        super(ImmutableMap.of(MemoryModuleType.AVOID_TARGET, MemoryModuleState.VALUE_PRESENT));
        this.range = range;
    }

    @Override
    protected void run(ServerWorld world, E mobEntity, long time) {
        LivingEntity livingEntity = this.getAvoidTarget(mobEntity);
        if (!mobEntity.canTarget(livingEntity)
                || isAvoidTargetOutOfRange(mobEntity)
                || cannotReachTarget(mobEntity)
                || isAvoidTargetDead(mobEntity)
                || isAvoidTargetInAnotherWorld(mobEntity)) {
            this.forgetAvoidTarget(mobEntity);
        }
    }

    private static <E extends LivingEntity> boolean cannotReachTarget(E entity) {
        Optional<Long> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        return optional.isPresent() && entity.world.getTime() - optional.get() > 200L;
    }

    private LivingEntity getAvoidTarget(E entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.AVOID_TARGET).get();
    }

    private boolean isAvoidTargetDead(E entity) {
        Optional<LivingEntity> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.AVOID_TARGET);
        return optional.isPresent() && !optional.get().isAlive();
    }

    private boolean isAvoidTargetOutOfRange(E entity) {
        LivingEntity target = getAvoidTarget(entity);
        return !entity.getPos().isInRange(target.getPos(), this.range);
    }

    private boolean isAvoidTargetInAnotherWorld(E entity) {
        return this.getAvoidTarget(entity).world != entity.world;
    }

    protected void forgetAvoidTarget(E entity) {
        entity.getBrain().forget(MemoryModuleType.AVOID_TARGET);
    }

}
