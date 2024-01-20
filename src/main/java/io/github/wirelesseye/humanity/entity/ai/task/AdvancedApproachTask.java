package io.github.wirelesseye.humanity.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.function.Function;

public class AdvancedApproachTask extends Task<HumanEntity> {
    private static final int WEAPON_REACH_REDUCTION = 1;
    private final Function<LivingEntity, Float> speed;

    public AdvancedApproachTask(float speed) {
        this((LivingEntity livingEntity) -> Float.valueOf(speed));
    }

    public AdvancedApproachTask(Function<LivingEntity, Float> speed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.REGISTERED));
        this.speed = speed;
    }

    @Override
    protected void run(ServerWorld serverWorld, HumanEntity entity, long l) {
        LivingEntity livingEntity = entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        if (LookTargetUtil.isVisibleInMemory(entity, livingEntity) && LookTargetUtil.isTargetWithinAttackRange(entity, livingEntity, WEAPON_REACH_REDUCTION)) {
            this.forgetWalkTarget(entity);
        } else {
            this.rememberWalkTarget(entity, livingEntity);
        }
        entity.setSprinting(true);
    }

    @Override
    protected void finishRunning(ServerWorld world, HumanEntity entity, long time) {
        entity.setSprinting(false);
    }

    private void rememberWalkTarget(LivingEntity entity, LivingEntity target) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
        WalkTarget walkTarget = new WalkTarget(new EntityLookTarget(target, false), this.speed.apply(entity).floatValue(), 0);
        brain.remember(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    private void forgetWalkTarget(LivingEntity entity) {
        entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }
}
