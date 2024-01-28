package io.github.wirelesseye.humanity.block;

import io.github.wirelesseye.humanity.Humanity;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllBlocks {
    public static final Block DOORPLATE_BLOCK = new Doorplate();

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(Humanity.ID, "doorplate"), DOORPLATE_BLOCK);
    }
}
