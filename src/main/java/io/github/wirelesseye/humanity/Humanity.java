package io.github.wirelesseye.humanity;

import io.github.wirelesseye.humanity.block.AllBlockEntityTypes;
import io.github.wirelesseye.humanity.block.AllBlocks;
import io.github.wirelesseye.humanity.entity.AllEntityTypes;
import io.github.wirelesseye.humanity.gui.AllScreenHandlerTypes;
import com.mojang.logging.LogUtils;
import io.github.wirelesseye.humanity.item.AllItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Humanity implements ModInitializer {
    public static final String ID = "humanity";

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        AllEntityTypes.register();
        AllItems.register();
        AllScreenHandlerTypes.register();
        AllBlocks.register();
        AllBlockEntityTypes.register();
        LOGGER.info("Humanity loaded");
    }
}
