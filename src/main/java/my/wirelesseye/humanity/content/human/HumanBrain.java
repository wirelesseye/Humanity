package my.wirelesseye.humanity.content.human;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import my.wirelesseye.humanity.AllEntityTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.*;


public class HumanBrain {
    protected static Brain<HumanEntity> create(Brain<HumanEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
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
                Pair.of(1, new WanderAroundTask())));
    }

    private static void addIdleActivities(Brain<HumanEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(
                Pair.of(2, new RandomTask<>(ImmutableList.of(
                        Pair.of(new FindWalkTargetTask(0.5f), 1),
                        Pair.of(new GoTowardsLookTarget(0.5f, 2), 1),
                        Pair.of(new WaitTask(30, 60), 1)))),
                createFreeFollowTask()));
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
        brain.resetPossibleActivities(ImmutableList.of(Activity.IDLE));
    }
}
