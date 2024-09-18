package net.fabricmc.renew_auto_plus;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class BanishedEntityModel extends BipedEntityModel<BanishedEntity>{
    public BanishedEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, red, green, blue, 0.2f);
    }
}
