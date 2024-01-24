package io.github.wirelesseye.humanity.entity.human;

import io.github.wirelesseye.humanity.entity.ai.AllMemoryModuleTypes;
import io.github.wirelesseye.humanity.entity.ai.sensor.AllSensorTypes;
import io.github.wirelesseye.humanity.gui.human.HumanScreenHandler;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import io.github.wirelesseye.humanity.entity.player.PlayerEntityTrait;
import io.github.wirelesseye.humanity.util.NameGenerator;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;


public class HumanEntity extends PassiveEntity implements InventoryOwner {
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
            MemoryModuleType.MOBS,
            MemoryModuleType.VISIBLE_MOBS,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER,
            AllMemoryModuleTypes.NEAREST_MONSTERS,
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
            AllSensorTypes.NEAREST_MONSTERS,
            SensorType.HURT_BY);

    private static final Identifier STEVE_SKIN = new Identifier("textures/entity/steve.png");
    private static final Identifier ALEX_SKIN = new Identifier("textures/entity/alex.png");

    private final HumanInventory inventory = new HumanInventory(this);
    private final HumanHungerManager hungerManager = new HumanHungerManager();

    private static final NameGenerator nameGenerator = new NameGenerator();
    private String lastName;
    private static final TrackedData<Boolean> SLIM = DataTracker.registerData(HumanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Nullable private UUID leaderPlayerUuid;

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
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        setFirstName(nameGenerator.generateFirstName());
        setLastName(nameGenerator.generateLastName());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SLIM, this.getRandom().nextBoolean());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("Inventory", this.inventory.writeNbt(new NbtList()));
        nbt.putInt("SelectedItemSlot", this.inventory.selectedSlot);
        this.hungerManager.writeNbt(nbt);
        nbt.putString("LastName", this.lastName);
        nbt.putBoolean("Slim", this.isSlim());
        if (this.leaderPlayerUuid != null) {
            nbt.putUuid("LeaderPlayer", this.leaderPlayerUuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.inventory.readNbt(nbt.getList("Inventory", NbtType.COMPOUND));
        this.inventory.selectedSlot = nbt.getInt("SelectedItemSlot");
        this.hungerManager.readNbt(nbt);
        this.lastName = nbt.getString("LastName");
        this.setIsSlim(nbt.getBoolean("Slim"));
        if (nbt.contains("LeaderPlayer")) {
            this.leaderPlayerUuid = nbt.getUuid("LeaderPlayer");
        } else {
            this.leaderPlayerUuid = null;
        }
    }

    public boolean isSlim() {
        return this.getDataTracker().get(SLIM);
    }

    public void setIsSlim(boolean isSlim) {
        this.getDataTracker().set(SLIM, isSlim);
    }

    public Identifier getSkinTexture() {
        return this.isSlim() ? ALEX_SKIN : STEVE_SKIN;
    }

    public String getFirstName() {
        return Objects.requireNonNull(getCustomName()).getString();
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        setCustomName(Text.of(firstName));
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public UUID getLeaderPlayerUuid() {
        return this.leaderPlayerUuid;
    }

    public void setLeaderPlayerUuid(@Nullable UUID uuid) {
        if (this.leaderPlayerUuid != null) {
            PlayerEntity oldPlayer = this.world.getPlayerByUuid(this.leaderPlayerUuid);
            if (oldPlayer != null) {
                ((PlayerEntityTrait) oldPlayer).humanity$removePartyMember(this.leaderPlayerUuid);
            }
        }

        if (uuid != null) {
            PlayerEntity player = this.world.getPlayerByUuid(uuid);
            if (player != null) {
                ((PlayerEntityTrait) player).humanity$addPartyMember(this.getUuid());
            }
        }
        this.leaderPlayerUuid = uuid;
    }

    @Override
    protected Brain.Profile<HumanEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<HumanEntity> deserializeBrain(Dynamic<?> dynamic) {
        return HumanBrainManager.create(this.createBrainProfile().deserialize(dynamic));
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
        HumanBrainManager.updateActivities(this);
        this.world.getProfiler().pop();
        super.mobTick();

        if (!this.world.isClient) {
            this.hungerManager.update(this);
        }
    }

    @Override
    public void tickMovement() {
        this.inventory.updateItems();
        this.tickHandSwing();
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
                    new HumanScreenHandler(i, playerInventory,  this.inventory, this),
                    this.getDisplayName()));
        }
        return ActionResult.success(this.world.isClient);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public HumanHungerManager getHungerManager() {
        return this.hungerManager;
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        if (mappedIndex >= 0 && mappedIndex < this.inventory.size()) {
            return StackReference.of(this.inventory, mappedIndex);
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    protected void loot(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getStack();

        if (itemStack.getItem() instanceof ArmorItem && this.inventory.tryReplaceArmor(itemStack)) {
            return;
        }

        this.inventory.insertStack(itemStack);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        this.inventory.vanishCursedItems();
        this.inventory.dropAll();
    }

    @Override
    protected void damageArmor(DamageSource source, float amount) {
        this.inventory.damageArmor(source, amount, HumanInventory.ARMOR_SLOTS);
    }

    @Override
    protected void damageHelmet(DamageSource source, float amount) {
        this.inventory.damageArmor(source, amount, HumanInventory.HELMET_SLOTS);
    }

    public boolean canFoodHeal() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }

    public boolean canConsume(boolean ignoreHunger) {
        return ignoreHunger || this.hungerManager.isNotFull();
    }

    @Override
    public ItemStack eatFood(World world, ItemStack stack) {
        this.getHungerManager().eat(stack.getItem(), stack);
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_BURP,
                SoundCategory.PLAYERS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
        return super.eatFood(world, stack);
    }

    public void addExhaustion(float exhaustion) {
        if (!this.world.isClient) {
            this.hungerManager.addExhaustion(exhaustion);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (super.tryAttack(target)) {
            this.addExhaustion(0.1f);
            return true;
        }
        return false;
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        super.applyDamage(source, amount);
        this.addExhaustion(source.getExhaustion());
    }

    @Override
    protected void jump() {
        super.jump();
        if (this.isSprinting()) {
            this.addExhaustion(0.2f);
        } else {
            this.addExhaustion(0.05f);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.travel(movementInput);
        this.addMovementExhaustion(this.getX() - d, this.getY() - e, this.getZ() - f);
    }

    public void addMovementExhaustion(double dx, double dy, double dz) {
        if (this.hasVehicle()) {
            return;
        }
        float v = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (this.isSwimming()) {
            int i = Math.round(v * 100.0f);
            if (i > 0) {
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else if (this.isSubmergedIn(FluidTags.WATER)) {
            int i = Math.round(v * 100.0f);
            if (i > 0) {
                this.addExhaustion(0.01f * (float)i * 0.01f);
            }
        } else {
            float v1 = (float) Math.sqrt(dx * dx + dz * dz);
            if (this.isTouchingWater()) {
                int i = Math.round(v1 * 100.0f);
                if (i > 0) {
                    this.addExhaustion(0.01f * (float)i * 0.01f);
                }
            }  else if (this.onGround) {
                int i = Math.round(v1 * 100.0f);
                if (i > 0) {
                    if (this.isSprinting()) {
                        this.addExhaustion(0.1f * (float)i * 0.01f);
                    } else if (this.isInSneakingPose()) {
                        this.addExhaustion(0.0f * (float)i * 0.01f);
                    } else {
                        this.addExhaustion(0.0f * (float)i * 0.01f);
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }
}
