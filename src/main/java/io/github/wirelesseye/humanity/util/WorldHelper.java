package io.github.wirelesseye.humanity.util;

import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public class WorldHelper {
    public static HumanEntity getClosestHuman(LivingEntity entity) {
        return getClosestHuman(entity, null);
    }

    public static HumanEntity getClosestHuman(LivingEntity entity, @Nullable Predicate<LivingEntity> predicate) {
        double followRange = getFollowRage(entity);
        return entity.world.getClosestEntity(
                HumanEntity.class,
                TargetPredicate.createAttackable().setBaseMaxDistance(followRange).setPredicate(predicate),
                entity,
                entity.getX(), entity.getEyeY(), entity.getZ(),
                entity.getBoundingBox().expand(followRange, 2, followRange));
    }

    public static double getFollowRage(LivingEntity entity) {
        return entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }
}
