package io.github.wirelesseye.humanity.entity.activategrid;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;

public class ActivateGridEntityModel extends EntityModel<ActivateGridEntity> {
    private final ModelPart root;

    public ActivateGridEntityModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public void setAngles(ActivateGridEntity entity, float limbAngle, float limbDistance, float animationProgress,
                          float headYaw, float headPitch) {}

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green,
                       float blue, float alpha) {
        matrices.push();
        matrices.multiply(Quaternion.fromEulerYxz(0, (float) Math.toRadians(180), 0));
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(
                "root",
                ModelPartBuilder.create()
                        .uv(0, 0)
                            .cuboid(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
