package my.wirelesseye.humanity;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Humanity implements ModInitializer {
    public static final String ID = "humanity";

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        AllEntityTypes.register();
        AllItems.register();
        LOGGER.info("Humanity loaded");
    }
}
