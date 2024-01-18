package my.wirelesseye.humanity.entity.human;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class HumanHungerManager {
    private int foodLevel = 20;
    private float saturationLevel = 5.0f;
    private float exhaustion;
    private int foodTickTimer;
    private int prevFoodLevel = 20;

    public void add(int food, float saturationModifier) {
        this.foodLevel = Math.min(food + this.foodLevel, 20);
        this.saturationLevel = Math.min(this.saturationLevel + (float)food * saturationModifier * 2.0f, (float)this.foodLevel);
    }

    public void eat(Item item, ItemStack stack) {
        if (item.isFood()) {
            FoodComponent foodComponent = item.getFoodComponent();
            this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
        }
    }

    public void update(HumanEntity human) {
        boolean bl;
        Difficulty difficulty = human.world.getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.exhaustion > 4.0f) {
            this.exhaustion -= 4.0f;
            if (this.saturationLevel > 0.0f) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0f, 0.0f);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        if ((bl = human.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION))
                && this.saturationLevel > 0.0f
                && human.canFoodHeal()
                && this.foodLevel >= 20) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 10) {
                float f = Math.min(this.saturationLevel, 6.0f);
                human.heal(f / 6.0f);
                this.addExhaustion(f);
                this.foodTickTimer = 0;
            }
        } else if (bl && this.foodLevel >= 18 && human.canFoodHeal()) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                human.heal(1.0f);
                this.addExhaustion(6.0f);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                if (human.getHealth() > 10.0f
                        || difficulty == Difficulty.HARD
                        || human.getHealth() > 1.0f && difficulty == Difficulty.NORMAL) {
                    human.damage(DamageSource.STARVE, 1.0f);
                }
                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("foodLevel", 99)) {
            this.foodLevel = nbt.getInt("foodLevel");
            this.foodTickTimer = nbt.getInt("foodTickTimer");
            this.saturationLevel = nbt.getFloat("foodSaturationLevel");
            this.exhaustion = nbt.getFloat("foodExhaustionLevel");
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("foodLevel", this.foodLevel);
        nbt.putInt("foodTickTimer", this.foodTickTimer);
        nbt.putFloat("foodSaturationLevel", this.saturationLevel);
        nbt.putFloat("foodExhaustionLevel", this.exhaustion);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public int getPrevFoodLevel() {
        return this.prevFoodLevel;
    }

    public boolean isNotFull() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0f);
    }

    public float getExhaustion() {
        return this.exhaustion;
    }

    public float getSaturationLevel() {
        return this.saturationLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public void setSaturationLevel(float saturationLevel) {
        this.saturationLevel = saturationLevel;
    }

    public void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }
}
