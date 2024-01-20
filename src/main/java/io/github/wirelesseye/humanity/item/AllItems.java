package io.github.wirelesseye.humanity.item;

import io.github.wirelesseye.humanity.Humanity;
import io.github.wirelesseye.humanity.entity.AllEntityTypes;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllItems {
    public static final Item HUMAN_SPAWN_EGG = new SpawnEggItem(AllEntityTypes.HUMAN, 0x00afaf, 0xffe3c2,
            new FabricItemSettings().group(ItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Humanity.ID, "human_spawn_egg"), HUMAN_SPAWN_EGG);
    }
}
