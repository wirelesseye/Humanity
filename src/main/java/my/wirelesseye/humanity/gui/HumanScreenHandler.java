package my.wirelesseye.humanity.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class HumanScreenHandler extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 41;
    private static final int ARMOR_OFFSET = 36;
    private static final int OFFHAND_OFFSET = 40;

    private static final Icon OFFHAND_ICON = new TextureIcon(new Texture(
            new Identifier("minecraft", "textures/item/empty_armor_slot_shield.png")));
    private static final Icon HEAD_ICON = new TextureIcon(new Texture(
            new Identifier("minecraft", "textures/item/empty_armor_slot_helmet.png")));
    private static final Icon CHEST_ICON = new TextureIcon(new Texture(
            new Identifier("minecraft", "textures/item/empty_armor_slot_chestplate.png")));
    private static final Icon LEGS_ICON = new TextureIcon(new Texture(
            new Identifier("minecraft", "textures/item/empty_armor_slot_leggings.png")));
    private static final Icon FEET_ICON = new TextureIcon(new Texture(
            new Identifier("minecraft", "textures/item/empty_armor_slot_boots.png")));

    private final WGridPanel root;

    public HumanScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE));
    }

    public HumanScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(AllScreenHandlerTypes.HUMAN, syncId, playerInventory);
        this.blockInventory = inventory;

        this.root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                WItemSlot itemSlot = WItemSlot.of(blockInventory, i * 9 + j);
                root.add(itemSlot, j, i + 1);
            }
        }

        WItemSlot offhandSlot = createEquipSlot(EquipmentSlot.OFFHAND, OFFHAND_ICON);
        root.add(offhandSlot, 0, 5);

        WItemSlot headSlot = createEquipSlot(EquipmentSlot.HEAD, HEAD_ICON);
        root.add(headSlot, 5, 5);

        WItemSlot chestSlot = createEquipSlot(EquipmentSlot.CHEST, CHEST_ICON);
        root.add(chestSlot, 6, 5);

        WItemSlot legsSlot = createEquipSlot(EquipmentSlot.LEGS, LEGS_ICON);
        root.add(legsSlot, 7, 5);

        WItemSlot feetSlot = createEquipSlot(EquipmentSlot.FEET, FEET_ICON);
        root.add(feetSlot, 8, 5);

        root.add(this.createPlayerInventoryPanel(), 0, 7);

        root.validate(this);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot slot = slots.get(index);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();

            if (slot.inventory == playerInventory
                    && slotStack.getItem() instanceof ArmorItem armorItem
                    && blockInventory.getStack(ARMOR_OFFSET + armorItem.getSlotType().getEntitySlotId()).isEmpty()) {
                blockInventory.setStack(ARMOR_OFFSET + armorItem.getSlotType().getEntitySlotId(), slotStack.copy());
                slotStack.setCount(0);
                this.root.validate(this);
                return ItemStack.EMPTY;
            }
        }

        return super.transferSlot(player, index);
    }

    private WItemSlot createEquipSlot(EquipmentSlot equipmentSlot, Icon icon) {
        boolean isArmor = equipmentSlot.getType() == EquipmentSlot.Type.ARMOR;

        int i;
        if (isArmor) {
            i = ARMOR_OFFSET + equipmentSlot.getEntitySlotId();
        } else {
            i = OFFHAND_OFFSET;
        }

        WItemSlot itemSlot = WItemSlot.of(this.blockInventory, i);

        if (isArmor) {
            itemSlot.setFilter(itemStack -> itemStack.getItem() instanceof ArmorItem
                    && ((ArmorItem) itemStack.getItem()).getSlotType() == equipmentSlot);
        }

        itemSlot.setIcon(icon);
        itemSlot.addChangeListener((slot, inventory, index, stack) -> {
            if (stack.isEmpty()) {
                slot.setIcon(icon);
            } else {
                slot.setIcon(null);
            }
        });

        return itemSlot;
    }
}
