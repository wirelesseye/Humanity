package io.github.wirelesseye.humanity.entity.activategrid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ActivateGridEntity extends Entity {
    public static final int TICK_LENGTH = 20;
    public static final int TICK_STEP = 2;
    private int tickLeft = 22;

    public ActivateGridEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        --this.tickLeft;
        if (!this.world.isClient && this.tickLeft <= 0) {
            this.discard();
        }
    }

    public float getAnimationProgress(float tickDelta) {
        int i = this.tickLeft - TICK_STEP;
        if (i <= 0) {
            return 1.0f;
        }
        return 1.0f - ((float) i - tickDelta) / (float) TICK_LENGTH;
    }
}
