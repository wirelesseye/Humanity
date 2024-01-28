package io.github.wirelesseye.humanity.item;

import io.github.wirelesseye.humanity.block.AllBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class DoorplateItem extends BlockItem {
    public DoorplateItem() {
        super(AllBlocks.DOORPLATE_BLOCK, new FabricItemSettings().group(ItemGroup.DECORATIONS));
    }
}
