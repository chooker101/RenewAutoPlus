package net.renew_auto_plus;

import org.jetbrains.annotations.Nullable;

//import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class FallenEntityRenderer extends MobEntityRenderer<FallenEntity, FallenEntityModel<FallenEntity>>{
    protected FallenEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new FallenEntityModel<FallenEntity>(ctx.getPart(RenewAutoPlusClientInitialize.FALLEN_MODEL_LAYER)), 0.5f);
        //this.addFeature(new BanishedArmorFeatureRenderer<BanishedEntity, BanishedEntityModel, BanishedEntityModel>(this, new BanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR)), new BanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR))));
    }

    @Override
    public Identifier getTexture(FallenEntity entity) {
        return new Identifier("renew_auto_plus", "textures/entity/fallen/fallen.png");
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(FallenEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        }
        if (showBody) {
            return RenderLayer.getEntityTranslucent(identifier);
        }
        if (showOutline) {
            return RenderLayer.getOutline(identifier);
        }
        return null;
    }
}
