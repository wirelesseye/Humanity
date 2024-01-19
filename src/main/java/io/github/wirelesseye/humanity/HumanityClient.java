package io.github.wirelesseye.humanity;

import io.github.wirelesseye.humanity.gui.AllScreenHandlerTypes;
import io.github.wirelesseye.humanity.entity.human.HumanEntityRenderer;
import io.github.wirelesseye.humanity.gui.HumanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class HumanityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(AllEntityTypes.HUMAN, HumanEntityRenderer::new);
        HandledScreens.register(AllScreenHandlerTypes.HUMAN, HumanScreen::new);
    }
}
