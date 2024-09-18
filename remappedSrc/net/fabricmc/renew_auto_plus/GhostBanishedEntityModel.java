package net.fabricmc.renew_auto_plus;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class GhostBanishedEntityModel extends BipedEntityModel<GhostBanishedEntity>{
    public GhostBanishedEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, 0.9412f, 0.3647f, 0.9255f, 0.4f);
    }
}
