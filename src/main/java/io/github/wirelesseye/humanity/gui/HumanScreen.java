package io.github.wirelesseye.humanity.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class HumanScreen extends CottonInventoryScreen<HumanScreenHandler> {
    public HumanScreen(HumanScreenHandler description, PlayerInventory inventory, Text title) {
        super(description, inventory, title);
    }
}
