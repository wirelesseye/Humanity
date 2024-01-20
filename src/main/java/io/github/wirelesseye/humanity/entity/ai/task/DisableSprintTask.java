package io.github.wirelesseye.humanity.entity.ai.task;

import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;


public class DisableSprintTask extends Task<HumanEntity> {
    public DisableSprintTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, HumanEntity entity) {
        return entity.isSprinting();
    }

    @Override
    protected void run(ServerWorld world, HumanEntity entity, long time) {
        entity.setSprinting(false);
    }
}
