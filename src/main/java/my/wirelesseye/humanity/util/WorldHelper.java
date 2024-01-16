package my.wirelesseye.humanity.util;

import my.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;


public class WorldHelper {
    public static HumanEntity getClosestHuman(LivingEntity entity) {
        double followRange = getFollowRage(entity);
        return entity.world.getClosestEntity(
                HumanEntity.class,
                TargetPredicate.createAttackable().setBaseMaxDistance(followRange),
                entity,
                entity.getX(), entity.getEyeY(), entity.getZ(),
                entity.getBoundingBox().expand(followRange, 2, followRange));
    }

    public static double getFollowRage(LivingEntity entity) {
        return entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }
}
