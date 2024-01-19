package io.github.wirelesseye.humanity.entity.human;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HumanEntityRenderer extends LivingEntityRenderer<HumanEntity, PlayerEntityModel<HumanEntity>> {
    private boolean slim = false;

    private final PlayerEntityModel<HumanEntity> normalModel;
    private final PlayerEntityModel<HumanEntity> slimModel;

    private final BipedEntityModel<HumanEntity> normalInnerArmorModel;
    private final BipedEntityModel<HumanEntity> slimInnerArmorModel;

    private final BipedEntityModel<HumanEntity> normalOuterArmorModel;
    private final BipedEntityModel<HumanEntity> slimOuterArmorModel;

    public HumanEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false), 0.5f);

        this.normalModel = new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false);
        this.slimModel = new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM), true);

        this.normalInnerArmorModel = new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR));
        this.slimInnerArmorModel = new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR));
        this.normalOuterArmorModel = new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR));
        this.slimOuterArmorModel = new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR));

        this.addFeature(new ArmorFeatureRenderer<>(this, this.normalInnerArmorModel, this.normalOuterArmorModel));
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(HumanEntity entity) {
        if (entity.isSlim() != this.slim) {
            this.slim = entity.isSlim();
            this.model = slim ? this.slimModel : this.normalModel;

            this.features.removeIf(feature -> feature instanceof ArmorFeatureRenderer<?,?,?>);
            this.addFeature(new ArmorFeatureRenderer<>(
                    this,
                    slim ? slimInnerArmorModel : normalInnerArmorModel,
                    slim ? slimOuterArmorModel : normalOuterArmorModel));
        }
        return entity.getSkinTexture();
    }

    @Override
    protected void scale(HumanEntity entity, MatrixStack matrices, float amount) {
        float g = 0.9375f;
        matrices.scale(g, g, g);
    }
}
