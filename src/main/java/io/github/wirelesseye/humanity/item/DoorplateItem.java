package io.github.wirelesseye.humanity.item;

import io.github.wirelesseye.humanity.block.AllBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;

public class DoorplateItem extends BlockItem {
    public DoorplateItem() {
        super(AllBlocks.DOORPLATE, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    }
}
