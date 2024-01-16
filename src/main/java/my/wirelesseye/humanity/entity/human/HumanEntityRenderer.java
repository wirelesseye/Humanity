package my.wirelesseye.humanity.entity.human;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HumanEntityRenderer extends LivingEntityRenderer<HumanEntity, PlayerEntityModel<HumanEntity>> {

    public HumanEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(HumanEntity entity) {
        return entity.getSkinTexture();
    }

    @Override
    protected void scale(HumanEntity entity, MatrixStack matrices, float amount) {
        float g = 0.9375f;
        matrices.scale(g, g, g);
    }
}
