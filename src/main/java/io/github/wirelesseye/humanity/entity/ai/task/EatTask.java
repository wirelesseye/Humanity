package io.github.wirelesseye.humanity.entity.ai.task;

import io.github.wirelesseye.humanity.entity.human.HumanEntity;
import io.github.wirelesseye.humanity.entity.human.HumanInventory;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;


public class EatTask extends Task<HumanEntity> {
    private ItemStack food;

    public EatTask() {
        super(ImmutableMap.of(), 20, 40);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, HumanEntity entity) {
        if (!entity.canConsume(false)) {
            return false;
        }

        if (entity.getMainHandStack().isFood()) {
            this.food = entity.getMainHandStack();
            return true;
        }

        if (entity.getOffHandStack().isFood()) {
            this.food = entity.getOffHandStack();
            return true;
        }

        HumanInventory inventory = (HumanInventory) entity.getInventory();

        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (stack.isFood()) {
                this.food = stack;
                inventory.selectedSlot = i;
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, HumanEntity entity, long time) {
        return true;
    }

    @Override
    protected void keepRunning(ServerWorld world, HumanEntity entity, long time) {
        if (time % 4 == 0) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_GENERIC_EAT,
                    SoundCategory.PLAYERS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
            world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.food), entity.getX(),
                    entity.getEyeY(), entity.getZ(), 1, 0, 0, 0, 0.15f);
        }
    }

    @Override
    protected void finishRunning(ServerWorld world, HumanEntity entity, long time) {
        entity.eatFood(world, this.food);
        this.food = null;
    }
}
