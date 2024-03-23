package net.fabricmc.renew_auto_plus;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class GhostBanishedEntityRender extends BipedEntityRenderer<GhostBanishedEntity, GhostBanishedEntityModel>{
    protected GhostBanishedEntityRender(EntityRendererFactory.Context ctx) {
        super(ctx, new GhostBanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM)), 0.5f);
        this.addFeature(new BanishedArmorFeatureRenderer<GhostBanishedEntity, GhostBanishedEntityModel, GhostBanishedEntityModel>(this, new GhostBanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_INNER_ARMOR)), new GhostBanishedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR))));
    }

    @Override
    public Identifier getTexture(GhostBanishedEntity entity) {
        return new Identifier("renew_auto_plus", "textures/entity/banished/banished.png");
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(GhostBanishedEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        }
        if (showBody) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        }
        if (showOutline) {
            return RenderLayer.getOutline(identifier);
        }
        return null;
    }
}
