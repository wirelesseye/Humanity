package my.wirelesseye.humanity;

import my.wirelesseye.humanity.content.human.HumanEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

public class HumanityClient implements ClientModInitializer {
    public static final EntityModelLayer MODEL_HUMAN_LAYER = new EntityModelLayer(new Identifier(Humanity.ID,
            "human"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(AllEntityTypes.HUMAN, HumanEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_HUMAN_LAYER,
                () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64));
    }
}
