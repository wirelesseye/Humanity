package my.wirelesseye.humanity.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import my.wirelesseye.humanity.Humanity;
import my.wirelesseye.humanity.entity.human.HumanEntity;
import my.wirelesseye.humanity.gui.widget.WHealthBar;
import my.wirelesseye.humanity.gui.widget.WHungerBar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;


public class HumanScreenHandler extends SyncedGuiDescription {
    private static final Identifier HUMAN_REQUEST_C2S_MESSAGE = new Identifier(Humanity.ID, "human_request_s2c");
    private static final Identifier HUMAN_SYNC_S2C_MESSAGE = new Identifier(Humanity.ID, "human_sync_s2c");

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

    private final WPanel root;

    private boolean isSyncData;
    private final HumanEntity human;

    private float health = 0;
    private int foodLevel = 0;

    public HumanScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE), null);
    }

    public HumanScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
                              HumanEntity human) {
        super(AllScreenHandlerTypes.HUMAN, syncId, playerInventory);
        this.blockInventory = inventory;
        this.human = human;

        setTitleVisible(false);

        this.root = buildRootPanel();
        setRootPanel(root);
        root.validate(this);

        ScreenNetworking.of(this, NetworkSide.CLIENT).receive(HUMAN_SYNC_S2C_MESSAGE, buf -> {
            this.health = buf.readFloat();
            this.foodLevel = buf.readInt();
        });

        ScreenNetworking.of(this, NetworkSide.SERVER).receive(HUMAN_REQUEST_C2S_MESSAGE, buf -> {
            isSyncData = true;
        });

        ScreenNetworking.of(this, NetworkSide.CLIENT).send(HUMAN_REQUEST_C2S_MESSAGE, buf -> {});
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if (isSyncData) {
            ScreenNetworking.of(this, NetworkSide.SERVER).send(HUMAN_SYNC_S2C_MESSAGE, buf -> {
                buf.writeFloat(human.getHealth());
                buf.writeInt(human.getHungerManager().getFoodLevel());
            });
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void addPainters() {
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

    private WPanel buildRootPanel() {
        WTabPanel panel = new WTabPanel();
        panel.add(buildStatusPanel(), tab -> tab.icon(new ItemIcon(Items.PLAYER_HEAD))
                .tooltip(new TranslatableText("gui.humanity.status")));
        panel.add(buildInventoryPanel(), tab -> tab.icon(new ItemIcon(Items.CHEST))
                .tooltip(new TranslatableText("gui.humanity.inventory")));
        return panel;
    }

    private WPanel buildStatusPanel() {
        WGridPanel panel = new WGridPanel();
        panel.setInsets(Insets.ROOT_PANEL);

        WHealthBar healthBar = new WHealthBar(() -> this.health, 20, 9);
        panel.add(healthBar, 2, 1);

        WHungerBar hungerBar = new WHungerBar(() -> (float) this.foodLevel, 20, 9);
        panel.add(hungerBar, 2, 2);

        return panel;
    }

    private WPanel buildInventoryPanel() {
        WGridPanel panel = new WGridPanel();
        panel.setInsets(Insets.ROOT_PANEL);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                WItemSlot itemSlot = WItemSlot.of(blockInventory, i * 9 + j);
                panel.add(itemSlot, j, i);
            }
        }

        WItemSlot offhandSlot = createEquipSlot(EquipmentSlot.OFFHAND, OFFHAND_ICON);
        panel.add(offhandSlot, 0, 4);

        WItemSlot headSlot = createEquipSlot(EquipmentSlot.HEAD, HEAD_ICON);
        panel.add(headSlot, 5, 4);

        WItemSlot chestSlot = createEquipSlot(EquipmentSlot.CHEST, CHEST_ICON);
        panel.add(chestSlot, 6, 4);

        WItemSlot legsSlot = createEquipSlot(EquipmentSlot.LEGS, LEGS_ICON);
        panel.add(legsSlot, 7, 4);

        WItemSlot feetSlot = createEquipSlot(EquipmentSlot.FEET, FEET_ICON);
        panel.add(feetSlot, 8, 4);

        panel.add(this.createPlayerInventoryPanel(), 0, 6);

        return panel;
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
