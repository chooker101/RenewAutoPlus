package net.fabricmc.renew_auto_plus;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.util.Identifier;

public class VoidVillagerEntityRenderer extends MobEntityRenderer<VoidVillagerEntity, VillagerResemblingModel<VoidVillagerEntity>> {
    private static final Identifier TEXTURE = new Identifier("renew_auto_plus", "textures/entity/void_villager/void_villager.png");

    protected VoidVillagerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new VillagerResemblingModel<VoidVillagerEntity>(ctx.getPart(EntityModelLayers.VILLAGER)), 0.5f);
    }

    @Override
    public Identifier getTexture(VoidVillagerEntity entity) {
        return TEXTURE;
    }
}
