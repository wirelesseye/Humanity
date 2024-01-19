package io.github.wirelesseye.humanity;

import io.github.wirelesseye.humanity.entity.AllEntityTypes;
import io.github.wirelesseye.humanity.gui.AllScreenHandlerTypes;
import io.github.wirelesseye.humanity.entity.human.HumanEntityRenderer;
import io.github.wirelesseye.humanity.gui.HumanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class HumanityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(AllEntityTypes.HUMAN, HumanEntityRenderer::new);
        HandledScreens.register(AllScreenHandlerTypes.HUMAN, HumanScreen::new);
    }
}
