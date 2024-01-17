package my.wirelesseye.humanity.gui;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public class AllScreenHandlerTypes {
    public static final ScreenHandlerType<HumanScreenHandler> HUMAN = Registry.register(
            Registry.SCREEN_HANDLER,
            "human",
            new ScreenHandlerType<>(HumanScreenHandler::new));

    public static void register() {
    }
}
