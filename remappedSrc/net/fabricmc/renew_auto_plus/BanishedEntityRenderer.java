package net.fabricmc.renew_auto_plus;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class BanishedEntityRenderer extends BipedEntityRenderer<BanishedEntity, BanishedEntityModel>{
    protected BanishedEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM)), 0.5f);
        this.addFeature(new BanishedArmorFeatureRenderer<BanishedEntity, BanishedEntityModel, BanishedEntityModel>(this, new BanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR)), new BanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR))));
    }

    @Override
    public Identifier getTexture(BanishedEntity entity) {
        return new Identifier("renew_auto_plus", "textures/entity/banished/banished.png");
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(BanishedEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        }
        if (showBody) {
            if(entity.isShadowed()){
                return RenderLayer.getItemEntityTranslucentCull(identifier);
            }
            else {
                return ((Model)this.model).getLayer(identifier);
            }
        }
        if (showOutline) {
            return RenderLayer.getOutline(identifier);
        }
        return null;
    }
}
