package io.github.wirelesseye.humanity.entity.activategrid;

import io.github.wirelesseye.humanity.Humanity;
import io.github.wirelesseye.humanity.HumanityClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ActivateGridEntityRenderer extends EntityRenderer<ActivateGridEntity> {
    private static final Identifier TEXTURE = new Identifier(Humanity.ID, "textures/entity/activate_grid.png");
    private final ActivateGridEntityModel model;

    public ActivateGridEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new ActivateGridEntityModel(ctx.getPart(HumanityClient.ACTIVE_GRID_LAYER));
    }

    @Override
    public Identifier getTexture(ActivateGridEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ActivateGridEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light) {
        float progress = entity.getAnimationProgress(tickDelta);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(getRenderLayer(entity));

        if (progress <= 0.3f) {
            matrices.push();
            float scale = progress / 0.3f;
            matrices.scale(1, scale, 1);
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            matrices.pop();
        } else if (progress <= 0.5f) {
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            float alpha = (1.0f - progress) / 0.5f;
            this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, alpha);
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumerProvider, light);
    }

    protected RenderLayer getRenderLayer(ActivateGridEntity entity) {
        Identifier identifier = this.getTexture(entity);
        return RenderLayer.getEntityTranslucent(identifier);
    }
}
