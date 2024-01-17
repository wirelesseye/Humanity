package my.wirelesseye.humanity.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;

public class HumanScreenHandler extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 41;

    public HumanScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE));
    }

    public HumanScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(AllScreenHandlerTypes.HUMAN, syncId, playerInventory);
        this.blockInventory = inventory;

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                WItemSlot itemSlot = WItemSlot.of(blockInventory, i * 9 + j);
                root.add(itemSlot, j, i + 1);
            }
        }

        WItemSlot headSlot = WItemSlot.of(blockInventory, 39);
        headSlot.setFilter(itemStack -> itemStack.getItem() instanceof ArmorItem
                && ((ArmorItem) itemStack.getItem()).getSlotType() == EquipmentSlot.HEAD);
        root.add(headSlot, 5, 5);

        WItemSlot chestSlot = WItemSlot.of(blockInventory, 38);
        chestSlot.setFilter(itemStack -> itemStack.getItem() instanceof ArmorItem
                && ((ArmorItem) itemStack.getItem()).getSlotType() == EquipmentSlot.CHEST);
        root.add(chestSlot, 6, 5);

        WItemSlot legsSlot = WItemSlot.of(blockInventory, 37);
        legsSlot.setFilter(itemStack -> itemStack.getItem() instanceof ArmorItem
                && ((ArmorItem) itemStack.getItem()).getSlotType() == EquipmentSlot.LEGS);
        root.add(legsSlot, 7, 5);

        WItemSlot feetSlot = WItemSlot.of(blockInventory, 36);
        feetSlot.setFilter(itemStack -> itemStack.getItem() instanceof ArmorItem
                && ((ArmorItem) itemStack.getItem()).getSlotType() == EquipmentSlot.FEET);
        root.add(feetSlot, 8, 5);

        WItemSlot offhandSlot = WItemSlot.of(blockInventory, 40);
        root.add(offhandSlot, 0, 5);

        root.add(this.createPlayerInventoryPanel(), 0, 7);

        root.validate(this);
    }
}
