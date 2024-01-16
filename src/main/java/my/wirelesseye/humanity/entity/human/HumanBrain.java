package my.wirelesseye.humanity.entity.human;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import my.wirelesseye.humanity.AllEntityTypes;
import my.wirelesseye.humanity.entity.ai.AllMemoryModuleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;

import java.util.Optional;


public class HumanBrain {
    protected static Brain<HumanEntity> create(Brain<HumanEntity> brain) {
        HumanBrain.addCoreActivities(brain);
        HumanBrain.addIdleActivities(brain);
        HumanBrain.addFightActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<HumanEntity> brain) {
        brain.setTaskList(Activity.CORE, ImmutableList.of(
                Pair.of(0, new StayAboveWaterTask(0.8f)),
                Pair.of(0, new WalkTask(0.75f)),
                Pair.of(0, new OpenDoorsTask()),
                Pair.of(0, new LookAroundTask(45, 90)),
                Pair.of(1, new WanderAroundTask()),
                Pair.of(5, new WalkToNearestVisibleWantedItemTask<>(0.5f, false, 4))));
    }

    private static void addIdleActivities(Brain<HumanEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(
                Pair.of(2, new RandomTask<>(ImmutableList.of(
                        Pair.of(new FindWalkTargetTask(0.5f), 1),
                        Pair.of(new GoTowardsLookTarget(0.5f, 2), 1),
                        Pair.of(new WaitTask(30, 60), 1)))),
                Pair.of(2, new UpdateAttackTargetTask<>(HumanBrain::getAttackTarget)),
                createFreeFollowTask()));
    }

    private static void addFightActivities(Brain<HumanEntity> brain) {
        brain.setTaskList(Activity.FIGHT, ImmutableList.of(
                Pair.of(0, new ForgetAttackTargetTask<>()),
                Pair.of(0, new MeleeAttackTask(20)),
                Pair.of(2, new RangedApproachTask(0.75f))
        ), ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT)));
    }

    private static Pair<Integer, Task<LivingEntity>> createFreeFollowTask() {
        return Pair.of(5, new RandomTask<>(ImmutableList.of(
                Pair.of(new FollowMobTask(EntityType.CAT, 8.0f), 8),
                Pair.of(new FollowMobTask(AllEntityTypes.HUMAN, 8.0f), 5),
                Pair.of(new FollowMobTask(EntityType.PLAYER, 8.0f), 5),
                Pair.of(new FollowMobTask(SpawnGroup.CREATURE, 8.0f), 1),
                Pair.of(new FollowMobTask(SpawnGroup.WATER_CREATURE, 8.0f), 2),
                Pair.of(new FollowMobTask(SpawnGroup.AXOLOTLS, 8.0f), 1),
                Pair.of(new FollowMobTask(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0f), 1),
                Pair.of(new FollowMobTask(SpawnGroup.WATER_AMBIENT, 8.0f), 1),
                Pair.of(new FollowMobTask(SpawnGroup.MONSTER, 8.0f), 1),
                Pair.of(new WaitTask(30, 60), 2))));
    }

    public static void updateActivities(HumanEntity human) {
        Brain<HumanEntity> brain = human.getBrain();
        brain.resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

    private static Optional<? extends LivingEntity> getAttackTarget(HumanEntity human) {
        Brain<HumanEntity> brain = human.getBrain();
        var hurtBy = brain.getOptionalMemory(MemoryModuleType.HURT_BY_ENTITY);
        if (hurtBy.isPresent()) {
            return hurtBy;
        }
        return brain.getOptionalMemory(AllMemoryModuleTypes.NEAREST_MONSTERS);
    }
}
