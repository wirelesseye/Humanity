package my.wirelesseye.humanity;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllItems {
    public static final Item HUMAN_SPAWN_EGG = new SpawnEggItem(AllEntityTypes.HUMAN, 0xc4c4c4, 0xadadad,
            new FabricItemSettings().group(ItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Humanity.ID, "human_spawn_egg"), HUMAN_SPAWN_EGG);
    }
}
