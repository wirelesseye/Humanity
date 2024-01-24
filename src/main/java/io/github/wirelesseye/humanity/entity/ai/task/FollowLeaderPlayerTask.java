package io.github.wirelesseye.humanity.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;


public class FollowLeaderPlayerTask extends Task<HumanEntity> {
    private final float speed;
    private final int minRange;
    private final int maxRange;
    private final int sprintRange;

    public FollowLeaderPlayerTask(float speed, int minRange, int maxRange, int sprintRange) {
        super(ImmutableMap.of());
        this.speed = speed;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.sprintRange = sprintRange;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, HumanEntity entity) {
        return entity.getLeaderPlayerUuid() != null;
    }

    @Override
    protected void run(ServerWorld world, HumanEntity entity, long time) {
        Brain<HumanEntity> brain = entity.getBrain();
        PlayerEntity leaderPlayer = world.getPlayerByUuid(entity.getLeaderPlayerUuid());
        if (leaderPlayer != null && !brain.hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
            float distance = entity.distanceTo(leaderPlayer);
            if (distance > minRange && distance < maxRange) {
                brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(
                        new EntityLookTarget(leaderPlayer, false), this.speed, minRange));
                if (distance > sprintRange || leaderPlayer.isSprinting()) {
                    entity.setSprinting(true);
                }
            }
        }
    }
}
