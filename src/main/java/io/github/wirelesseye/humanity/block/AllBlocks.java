package io.github.wirelesseye.humanity.block;

import io.github.wirelesseye.humanity.Humanity;
import io.github.wirelesseye.humanity.block.doorplate.Doorplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllBlocks {
    public static final Doorplate DOORPLATE = new Doorplate();

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(Humanity.ID, "doorplate"), DOORPLATE);
    }
}
