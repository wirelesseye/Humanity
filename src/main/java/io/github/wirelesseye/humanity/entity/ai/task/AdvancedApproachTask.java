package io.github.wirelesseye.humanity.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import io.github.wirelesseye.humanity.mixin.CreeperEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public class AdvancedApproachTask extends Task<HumanEntity> {
    private static final int WEAPON_REACH_REDUCTION = 1;

    private final Function<LivingEntity, Float> speed;
    private final int fleeRange;

    public AdvancedApproachTask(float speed, int fleeRange) {
        this((LivingEntity livingEntity) -> speed, fleeRange);
    }

    public AdvancedApproachTask(Function<LivingEntity, Float> speed, int fleeRange) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.REGISTERED));
        this.speed = speed;
        this.fleeRange = fleeRange;
    }

    @Override
    protected void run(ServerWorld serverWorld, HumanEntity entity, long l) {
        LivingEntity target = getAttackTarget(entity);
        if (isTargetDanger(entity)) {
            if (!isWalkTargetPresentAndFar(entity) && entity.getPos().isInRange(target.getPos(), fleeRange)) {
                this.fleeTarget(entity, target);
            }
        } else if (LookTargetUtil.isVisibleInMemory(entity, target)
                && LookTargetUtil.isTargetWithinAttackRange(entity, target, WEAPON_REACH_REDUCTION)) {
            this.forgetWalkTarget(entity);
        } else {
            this.reachTarget(entity, target);
        }
    }

    private void reachTarget(LivingEntity entity, LivingEntity target) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
        WalkTarget walkTarget = new WalkTarget(new EntityLookTarget(target, false),
                this.speed.apply(entity), 0);
        brain.remember(MemoryModuleType.WALK_TARGET, walkTarget);
    }

    private void fleeTarget(PathAwareEntity entity, LivingEntity target) {
        Vec3d pos = target.getPos();
        for (int i = 0; i < 10; ++i) {
            Vec3d vec3d = FuzzyTargeting.findFrom(entity, 16, 7, pos);
            if (vec3d == null) continue;
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed.apply(entity),
                    0));
            return;
        }
    }

    private LivingEntity getAttackTarget(HumanEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    private void forgetWalkTarget(LivingEntity entity) {
        entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }

    private boolean isWalkTargetPresentAndFar(HumanEntity entity) {
        LivingEntity target = getAttackTarget(entity);

        if (!entity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
            return false;
        }
        WalkTarget walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        if (walkTarget.getSpeed() != this.speed.apply(entity)) {
            return false;
        }
        Vec3d vec3d = walkTarget.getLookTarget().getPos().subtract(entity.getPos());
        return vec3d.dotProduct(target.getPos().subtract(entity.getPos())) < 0.0;
    }

    private boolean isTargetDanger(HumanEntity entity) {
        LivingEntity target = getAttackTarget(entity);
        return target instanceof CreeperEntity creeper
                && ((CreeperEntityAccessor) creeper).getCurrentFuseTime() > 5;
    }
}
