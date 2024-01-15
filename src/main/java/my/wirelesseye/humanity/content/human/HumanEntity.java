package my.wirelesseye.humanity.content.human;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class HumanEntity extends PassiveEntity implements InventoryOwner {
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.MOBS,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.DOORS_TO_CLOSE,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY);
    private static final ImmutableList<SensorType<? extends Sensor<? super HumanEntity>>> SENSORS = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY);

    private final HumanInventory inventory = new HumanInventory(this);

    public HumanEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        this.getNavigation().setCanSwim(true);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.setCanPickUpLoot(true);
    }

    public static DefaultAttributeContainer.Builder createHumanAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED)
                .add(EntityAttributes.GENERIC_LUCK);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Inventory", this.inventory.writeNbt(new NbtList()));
        nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.inventory.readNbt(nbt.getList("Inventory", NbtType.COMPOUND));
        this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
    }

    public Identifier getSkinTexture() {
        return DefaultSkinHelper.getTexture();
    }

    @Override
    protected Brain.Profile<HumanEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<HumanEntity> deserializeBrain(Dynamic<?> dynamic) {
        return HumanBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<HumanEntity> getBrain() {
        return (Brain<HumanEntity>) super.getBrain();
    }

    @Override
    protected void mobTick() {
        this.world.getProfiler().push("humanBrain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("humanActivityUpdate");
        HumanBrain.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    @Override
    public void tickMovement() {
        this.inventory.updateItems();
        super.tickMovement();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.world.isClient && !player.isSpectator()) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    new GenericContainerScreenHandler(
                            ScreenHandlerType.GENERIC_9X4, i, playerInventory, this.inventory, 4),
                    this.getDisplayName()));
        }
        return ActionResult.success(this.world.isClient);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        if (mappedIndex >= 0 && mappedIndex < this.inventory.size()) {
            return StackReference.of(this.inventory, mappedIndex);
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        this.inventory.insertStack(itemStack);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        this.inventory.vanishCursedItems();
        this.inventory.dropAll();
    }
}
