package io.github.wirelesseye.humanity.entity.ai.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.wirelesseye.humanity.entity.ai.AllMemoryModuleTypes;
import io.github.wirelesseye.humanity.mixin.CreeperEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;
import java.util.Set;

public class NearestMonstersSensor extends Sensor<LivingEntity> {
    private boolean matchMonsters(LivingEntity entity, LivingEntity target) {
        float distance = 10.0f;
        return target instanceof Monster
                && !(target instanceof Angerable)
                && !(target instanceof PiglinEntity)
                && target.squaredDistanceTo(entity) <= (double) (distance * distance);
    }

    private boolean matchDanger(LivingEntity entity, LivingEntity target) {
        if (entity.getHealth() < 5) {
            return true;
        }

        float distance = 10.0f;
        return target instanceof CreeperEntity creeper
                && ((CreeperEntityAccessor) creeper).getCurrentFuseTime() > 5
                && target.squaredDistanceTo(entity) <= (double) (distance * distance);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        entity.getBrain().remember(AllMemoryModuleTypes.NEAREST_MONSTERS, this.getNearestVisibleMonster(entity));
        entity.getBrain().remember(MemoryModuleType.AVOID_TARGET, this.getNearestVisibleDanger(entity));
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(AllMemoryModuleTypes.NEAREST_MONSTERS, MemoryModuleType.AVOID_TARGET);
    }

    private Optional<LivingEntity> getNearestVisibleMonster(LivingEntity entity) {
        return this.getVisibleLivingEntities(entity).flatMap(livingTargetCache ->
                livingTargetCache.findFirst(livingEntity2 -> this.matchMonsters(entity, livingEntity2)));
    }

    private Optional<LivingEntity> getNearestVisibleDanger(LivingEntity entity) {
        return this.getVisibleLivingEntities(entity).flatMap(livingTargetCache ->
                livingTargetCache.findFirst(livingEntity2 -> this.matchDanger(entity, livingEntity2)));
    }

    protected Optional<LivingTargetCache> getVisibleLivingEntities(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
    }
}
