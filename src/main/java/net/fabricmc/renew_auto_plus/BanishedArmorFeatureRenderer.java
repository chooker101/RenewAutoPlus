package net.fabricmc.renew_auto_plus;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

//Should be converted to a Mixin, maybe, it is easier to keep player on real ArmorFeatureRenderer
@Environment(value=EnvType.CLIENT)
public class BanishedArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>>
extends FeatureRenderer<T, M> {
    private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
    private final A leggingsModel;
    private final A bodyModel;

    public BanishedArmorFeatureRenderer(FeatureRendererContext<T, M> context, A leggingsModel, A bodyModel) {
        super(context);
        this.leggingsModel = leggingsModel;
        this.bodyModel = bodyModel;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i, this.getArmor(EquipmentSlot.CHEST));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i, this.getArmor(EquipmentSlot.LEGS));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i, this.getArmor(EquipmentSlot.FEET));
        this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i, this.getArmor(EquipmentSlot.HEAD));
    }

    private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model) {
        ItemStack itemStack = ((LivingEntity)entity).getEquippedStack(armorSlot);
        if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)itemStack.getItem();
        if (armorItem.getSlotType() != armorSlot) {
            return;
        }
        ((BipedEntityModel<T>)this.getContextModel()).setAttributes(model);
        this.setVisible(armorSlot, model);
        boolean bl = this.usesSecondLayer(armorSlot);
        boolean bl2 = itemStack.hasGlint();
        float a = 1.0f;
        if(entity instanceof BanishedEntity) {
            a = ((BanishedEntity)entity).getArmorAlpha();
        }
        if (armorItem instanceof DyeableArmorItem) {
            int i = ((DyeableArmorItem)armorItem).getColor(itemStack);
            float f = (float)(i >> 16 & 0xFF) / 255.0f;
            float g = (float)(i >> 8 & 0xFF) / 255.0f;
            float h = (float)(i & 0xFF) / 255.0f;
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, a, null);
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0f, 1.0f, 1.0f, a, "overlay");
        } else {
            this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0f, 1.0f, 1.0f, a, null);
        }
    }

    protected void setVisible(EquipmentSlot slot, A model) {
        ((BipedEntityModel<T>)model).setVisible(false);
        ((BipedEntityModel<T>)leggingsModel).setVisible(false);
        switch (slot) {
            case HEAD: {
                ((BipedEntityModel<T>)model).head.visible = true;
                ((BipedEntityModel<T>)model).hat.visible = true;
                break;
            }
            case CHEST: {
                ((BipedEntityModel<T>)model).body.visible = true;
                ((BipedEntityModel<T>)model).rightArm.visible = true;
                ((BipedEntityModel<T>)model).leftArm.visible = true;
                break;
            }
            case LEGS: {
                ((BipedEntityModel<T>)model).body.visible = true;
                ((BipedEntityModel<T>)model).rightLeg.visible = true;
                ((BipedEntityModel<T>)model).leftLeg.visible = true;
                break;
            }
            case FEET: {
                ((BipedEntityModel<T>)model).rightLeg.visible = true;
                ((BipedEntityModel<T>)model).leftLeg.visible = true;
            }
            case MAINHAND: {}
            case OFFHAND: {}
        }
    }

    //Could just Mixin this
    private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, float alpha, @Nullable String overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(alpha < 0.9f ? RenderLayer.getItemEntityTranslucentCull(this.getArmorTexture(item, legs, overlay)) : RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, legs, overlay)));
        ((AnimalModel<T>)model).render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
    }

    private A getArmor(EquipmentSlot slot) {
        return this.usesSecondLayer(slot) ? this.leggingsModel : this.bodyModel;
    }

    private boolean usesSecondLayer(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    private Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay) {
        String string = "textures/models/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : 1) + (String)(overlay == null ? "" : "_" + overlay) + ".png";
        return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, Identifier::new);
    }
}
