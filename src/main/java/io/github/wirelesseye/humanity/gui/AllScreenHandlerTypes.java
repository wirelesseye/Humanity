package io.github.wirelesseye.humanity.gui;

import io.github.wirelesseye.humanity.gui.human.HumanScreenHandler;
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
