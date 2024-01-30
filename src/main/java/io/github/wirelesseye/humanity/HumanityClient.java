package io.github.wirelesseye.humanity;

import io.github.wirelesseye.humanity.block.AllBlocks;
import io.github.wirelesseye.humanity.entity.AllEntityTypes;
import io.github.wirelesseye.humanity.entity.activategrid.ActivateGridEntityModel;
import io.github.wirelesseye.humanity.entity.activategrid.ActivateGridEntityRenderer;
import io.github.wirelesseye.humanity.gui.AllScreenHandlerTypes;
import io.github.wirelesseye.humanity.entity.human.HumanEntityRenderer;
import io.github.wirelesseye.humanity.gui.human.HumanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class HumanityClient implements ClientModInitializer {
    public static final EntityModelLayer ACTIVE_GRID_LAYER = new EntityModelLayer(new Identifier(Humanity.ID, "active_grid"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(AllEntityTypes.HUMAN, HumanEntityRenderer::new);
        HandledScreens.register(AllScreenHandlerTypes.HUMAN, HumanScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(AllBlocks.DOORPLATE_BLOCK, RenderLayer.getTranslucent());
        EntityRendererRegistry.register(AllEntityTypes.ACTIVATE_GRID, ActivateGridEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ACTIVE_GRID_LAYER, ActivateGridEntityModel::getTexturedModelData);
    }
}
