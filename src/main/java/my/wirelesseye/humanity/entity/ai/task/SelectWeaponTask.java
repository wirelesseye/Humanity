package my.wirelesseye.humanity.entity.ai.task;

import my.wirelesseye.humanity.entity.human.HumanEntity;
import my.wirelesseye.humanity.entity.human.HumanInventory;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;


public class SelectWeaponTask extends Task<HumanEntity> {
    private int counter = 0;

    public SelectWeaponTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, HumanEntity entity) {
        Item selectedItem = entity.getMainHandStack().getItem();
        return this.counter-- <= 0 && !(selectedItem instanceof SwordItem) && !(selectedItem instanceof AxeItem);
    }

    @Override
    protected void run(ServerWorld world, HumanEntity entity, long time) {
        ((HumanInventory) entity.getInventory()).selectPreferedMeleeWeapon();
        counter = 100; // Select weapon every 100 ticks
    }
}
