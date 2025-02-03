package net.renew_auto_plus;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class FrilledBasiliskEntityRenderer extends MobEntityRenderer<FrilledBasiliskEntity, FrilledBasiliskEntityModel<FrilledBasiliskEntity>> {
    private static final Identifier BLACK_TEXTURE = new Identifier("renew_auto_plus", "textures/entity/frilled_basilisk/black.png");
    private static final Identifier GREEN_TEXTURE = new Identifier("renew_auto_plus", "textures/entity/frilled_basilisk/green.png");
    private static final Identifier RED_TEXTURE = new Identifier("renew_auto_plus", "textures/entity/frilled_basilisk/red.png");

    public FrilledBasiliskEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FrilledBasiliskEntityModel<FrilledBasiliskEntity>(context.getPart(RenewAutoPlusClientInitialize.FRILLED_BASILISK_MODEL_LAYER)), 0.3f);
    }

    @Override
    public Identifier getTexture(FrilledBasiliskEntity basiliskEntity) {
        return switch (basiliskEntity.getVariant()) {
            default -> throw new IncompatibleClassChangeError();
            case BLACK -> BLACK_TEXTURE;
            case GREEN -> GREEN_TEXTURE;
            case RED -> RED_TEXTURE;
        };
    }
}
