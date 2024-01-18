package io.github.wirelesseye.humanity.entity.human;

import com.google.common.collect.ImmutableList;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import java.util.List;

public class HumanInventory implements Inventory, Nameable {
    public final HumanEntity human;
    public int selectedSlot = 0;

    public final DefaultedList<ItemStack> main = DefaultedList.ofSize(36, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> armor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public final DefaultedList<ItemStack> offHand = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand);

    public static final int[] ARMOR_SLOTS = new int[]{0, 1, 2, 3};
    public static final int[] HELMET_SLOTS = new int[]{3};

    public HumanInventory(HumanEntity human) {
        this.human = human;
    }

    @Override
    public int size() {
        return this.main.size() + this.armor.size() + this.offHand.size();
    }

    public int getEmptySlot() {
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.main.get(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.main) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        for (ItemStack itemStack : this.armor) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        for (ItemStack itemStack : this.offHand) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        MappedSlot mappedSlot = getMappedSlot(slot);
        return mappedSlot.subInventory == null ? ItemStack.EMPTY : mappedSlot.subInventory.get(mappedSlot.slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        MappedSlot mappedSlot = getMappedSlot(slot);
        if (mappedSlot.subInventory != null && !mappedSlot.subInventory.get(mappedSlot.slot).isEmpty()) {
            return Inventories.splitStack(mappedSlot.subInventory, mappedSlot.slot, amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        MappedSlot mappedSlot = getMappedSlot(slot);
        if (mappedSlot.subInventory != null && !mappedSlot.subInventory.get(mappedSlot.slot).isEmpty()) {
            ItemStack itemStack = mappedSlot.subInventory.get(mappedSlot.slot);
            mappedSlot.subInventory.set(mappedSlot.slot, ItemStack.EMPTY);
            return itemStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        MappedSlot mappedSlot = getMappedSlot(slot);
        if (mappedSlot.subInventory != null) {
            mappedSlot.subInventory.set(mappedSlot.slot, stack);
        }
    }

    private static final class MappedSlot {
        public DefaultedList<ItemStack> subInventory;
        public int slot;

        public MappedSlot(DefaultedList<ItemStack> subInventory, int slot) {
            this.subInventory = subInventory;
            this.slot = slot;
        }
    }

    private MappedSlot getMappedSlot(int slot) {
        DefaultedList<ItemStack> subInventory = null;
        for (DefaultedList<ItemStack> inventory : this.combinedInventory) {
            if (slot < inventory.size()) {
                subInventory = inventory;
                break;
            }
            slot -= inventory.size();
        }
        return new MappedSlot(subInventory, slot);
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return !(player.squaredDistanceTo(this.human) > 64.0);
    }

    @Override
    public void clear() {
        for (List<ItemStack> list : this.combinedInventory) {
            list.clear();
        }
    }

    @Override
    public Text getName()  {
        return new TranslatableText("container.inventory");
    }

    public NbtList writeNbt(NbtList nbtList) {
        NbtCompound nbtCompound;
        int i;
        for (i = 0; i < this.main.size(); ++i) {
            if (this.main.get(i).isEmpty()) continue;
            nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            this.main.get(i).writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        for (i = 0; i < this.armor.size(); ++i) {
            if (this.armor.get(i).isEmpty()) continue;
            nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)(i + 100));
            this.armor.get(i).writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        for (i = 0; i < this.offHand.size(); ++i) {
            if (this.offHand.get(i).isEmpty()) continue;
            nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)(i + 150));
            this.offHand.get(i).writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }

    public void readNbt(NbtList nbtList) {
        this.main.clear();
        this.armor.clear();
        this.offHand.clear();
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
            if (itemStack.isEmpty()) continue;
            if (j >= 0 && j < this.main.size()) {
                this.main.set(j, itemStack);
                continue;
            }
            if (j >= 100 && j < this.armor.size() + 100) {
                this.armor.set(j - 100, itemStack);
                continue;
            }
            if (j < 150 || j >= this.offHand.size() + 150) continue;
            this.offHand.set(j - 150, itemStack);
        }
    }

    public void vanishCursedItems() {
        for (int i = 0; i < size(); ++i) {
            ItemStack itemStack = getStack(i);
            if (itemStack.isEmpty() || !EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            removeStack(i);
        }
    }

    public void dropAll() {
        for (DefaultedList<ItemStack> subInventory : this.combinedInventory) {
            for (int i = 0; i < subInventory.size(); ++i) {
                ItemStack itemStack = subInventory.get(i);
                if (itemStack.isEmpty()) continue;
                this.human.dropStack(itemStack);
                subInventory.set(i, ItemStack.EMPTY);
            }
        }
    }

    public void updateItems() {
        for (DefaultedList<ItemStack> defaultedList : this.combinedInventory) {
            for (int i = 0; i < defaultedList.size(); ++i) {
                if (defaultedList.get(i).isEmpty()) continue;
                defaultedList.get(i).inventoryTick(this.human.world, this.human, i, this.selectedSlot == i);
            }
        }
        if (!human.world.isClient) {
            this.human.equipStack(EquipmentSlot.MAINHAND, getMainHandStack());
            this.human.equipStack(EquipmentSlot.OFFHAND, this.offHand.get(0));
            this.human.equipStack(EquipmentSlot.HEAD, this.armor.get(3));
            this.human.equipStack(EquipmentSlot.CHEST, this.armor.get(2));
            this.human.equipStack(EquipmentSlot.LEGS, this.armor.get(1));
            this.human.equipStack(EquipmentSlot.FEET, this.armor.get(0));
        }
    }

    private boolean canStackAddMore(ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() && ItemStack.canCombine(existingStack, stack) && existingStack.isStackable() && existingStack.getCount() < existingStack.getMaxCount() && existingStack.getCount() < this.getMaxCountPerStack();
    }

    public int getOccupiedSlotWithRoomForStack(ItemStack stack) {
        if (this.canStackAddMore(this.getStack(this.selectedSlot), stack)) {
            return this.selectedSlot;
        }
        if (this.canStackAddMore(this.getStack(40), stack)) {
            return 40;
        }
        for (int i = 0; i < this.main.size(); ++i) {
            if (!this.canStackAddMore(this.main.get(i), stack)) continue;
            return i;
        }
        return -1;
    }

    private int addStack(ItemStack stack) {
        int i = this.getOccupiedSlotWithRoomForStack(stack);
        if (i == -1) {
            i = this.getEmptySlot();
        }
        if (i == -1) {
            return stack.getCount();
        }
        return this.addStack(i, stack);
    }

    private int addStack(int slot, ItemStack stack) {
        int j;
        Item item = stack.getItem();
        int i = stack.getCount();
        ItemStack itemStack = this.getStack(slot);
        if (itemStack.isEmpty()) {
            itemStack = new ItemStack(item, 0);
            if (stack.hasNbt()) {
                itemStack.setNbt(stack.getNbt().copy());
            }
            this.setStack(slot, itemStack);
        }
        if ((j = i) > itemStack.getMaxCount() - itemStack.getCount()) {
            j = itemStack.getMaxCount() - itemStack.getCount();
        }
        if (j > this.getMaxCountPerStack() - itemStack.getCount()) {
            j = this.getMaxCountPerStack() - itemStack.getCount();
        }
        if (j == 0) {
            return i;
        }
        itemStack.increment(j);
        itemStack.setBobbingAnimationTime(5);
        return i -= j;
    }

    public boolean insertStack(ItemStack stack) {
        return this.insertStack(-1, stack);
    }

    public boolean insertStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        try {
            if (!stack.isDamaged()) {
                int i;
                do {
                    i = stack.getCount();
                    if (slot == -1) {
                        stack.setCount(this.addStack(stack));
                        continue;
                    }
                    stack.setCount(this.addStack(slot, stack));
                } while (!stack.isEmpty() && stack.getCount() < i);
                return stack.getCount() < i;
            }
            if (slot == -1) {
                slot = this.getEmptySlot();
            }
            if (slot >= 0) {
                this.main.set(slot, stack.copy());
                this.main.get(slot).setBobbingAnimationTime(5);
                stack.setCount(0);
                return true;
            }
            return false;
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Adding item to inventory");
            CrashReportSection crashReportSection = crashReport.addElement("Item being added");
            crashReportSection.add("Item ID", Item.getRawId(stack.getItem()));
            crashReportSection.add("Item data", stack.getDamage());
            crashReportSection.add("Item name", () -> stack.getName().getString());
            throw new CrashException(crashReport);
        }
    }

    public ItemStack getMainHandStack() {
        return this.main.get(this.selectedSlot);
    }

    public void damageArmor(DamageSource damageSource, float amount, int[] slots) {
        if (amount <= 0.0f) {
            return;
        }
        if ((amount /= 4.0f) < 1.0f) {
            amount = 1.0f;
        }
        for (int i : slots) {
            ItemStack itemStack = this.armor.get(i);
            if (damageSource.isFire() && itemStack.getItem().isFireproof()
                    || !(itemStack.getItem() instanceof ArmorItem)) continue;
            itemStack.damage((int)amount, this.human, human -> {
                EquipmentSlot slot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, i);
                human.sendEquipmentBreakStatus(slot);

                // Equip armor if exists
                ItemStack stack = ItemStack.EMPTY;
                int selectSlot = -1;
                for (int j = 0; j < this.main.size(); j++) {
                    ItemStack nextStack = this.main.get(j);
                    if (nextStack.getItem() instanceof ArmorItem armorItem
                            && armorItem.getSlotType() == slot
                            && prefersNewArmor(nextStack, stack)) {
                        stack = nextStack;
                        selectSlot = j;
                    }
                }
                if (!stack.isEmpty()) {
                    this.armor.set(i, stack);
                    this.main.set(selectSlot, ItemStack.EMPTY);
                }
            });
        }
    }

    public void replaceArmor(ItemStack stack, int slot) {
        ItemStack currentStack = this.armor.get(slot);
        if (!currentStack.isEmpty()) {
            if (getEmptySlot() != -1) {
                this.addStack(currentStack);
            } else {
                return;
            }
        }

        this.armor.set(slot, stack.copy());
        stack.setCount(0);
    }

    public boolean tryReplaceArmor(ItemStack itemStack) {
        EquipmentSlot slot = ((ArmorItem) itemStack.getItem()).getSlotType();
        if (prefersNewArmor(itemStack, armor.get(slot.getEntitySlotId()))) {
            this.replaceArmor(itemStack, slot.getEntitySlotId());
            return true;
        }

        return false;
    }

    static private boolean prefersNewArmor(ItemStack newArmor, ItemStack equippedArmor) {
        if (equippedArmor.isEmpty()) {
            return true;
        }
        ArmorItem newArmorItem = (ArmorItem) newArmor.getItem();
        Item equippedItem = equippedArmor.getItem();
        if (equippedItem instanceof ArmorItem equippedArmorItem) {
            if (equippedArmorItem.getSlotType() != newArmorItem.getSlotType()) {
                return false;
            }
            float newArmorScore = newArmorItem.getProtection() * 100
                    + newArmorItem.getToughness() * 10
                    + newArmorItem.getMaterial().getKnockbackResistance();
            float equippedArmorScore = equippedArmorItem.getProtection() * 100
                    + equippedArmorItem.getToughness() * 10
                    + equippedArmorItem.getMaterial().getKnockbackResistance();
            return newArmorScore > equippedArmorScore;
        } else {
            return false;
        }
    }

    public void selectPreferedMeleeWeapon() {
        ItemStack stack = ItemStack.EMPTY;
        int selectSlot = -1;
        for (int j = 0; j < this.main.size(); j++) {
            ItemStack nextStack = this.main.get(j);
            if ((nextStack.getItem() instanceof SwordItem || nextStack.getItem() instanceof AxeItem)
                    && prefersMeleeWeapon(nextStack, stack)) {
                stack = nextStack;
                selectSlot = j;
            }
        }
        if (!stack.isEmpty()) {
            this.selectedSlot = selectSlot;
        }
    }

    static private boolean prefersMeleeWeapon(ItemStack newWeapon, ItemStack equippedWeapon) {
        if (equippedWeapon.isEmpty()) {
            return true;
        }

        float newWeaponDamage;
        if (newWeapon.getItem() instanceof SwordItem swordItem) {
            newWeaponDamage = swordItem.getAttackDamage();
        } else if (newWeapon.getItem() instanceof AxeItem axeItem) {
            newWeaponDamage = axeItem.getAttackDamage();
        } else {
            return false;
        }

        float equippedWeaponDamage;
        if (equippedWeapon.getItem() instanceof SwordItem swordItem) {
            equippedWeaponDamage = swordItem.getAttackDamage();
        } else if (equippedWeapon.getItem() instanceof AxeItem axeItem) {
            equippedWeaponDamage = axeItem.getAttackDamage();
        } else {
            return true;
        }

        return newWeaponDamage > equippedWeaponDamage;
    }
}
