package net.fabricmc.renew_auto_plus;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class FrilledBasiliskEntityModel<T extends FrilledBasiliskEntity> extends EntityModel<T> {
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart foot_bl;
	private final ModelPart foot_fr;
	private final ModelPart foot_br;
	private final ModelPart foot_fl;
	private final ModelPart frill_right;
	private final ModelPart frill_left;
	private final ModelPart frill_right_extended;
	private final ModelPart frill_left_extended;
	private final ModelPart head_lower;
	private final ModelPart head_upper;

	public FrilledBasiliskEntityModel(ModelPart root) {
		this.body = root.getChild("body");
		this.tail = body.getChild("tail");
		this.foot_bl = body.getChild("foot_bl");
		this.foot_fr = body.getChild("foot_fr");
		this.foot_br = body.getChild("foot_br");
		this.foot_fl = body.getChild("foot_fl");
		this.frill_right = body.getChild("frill_right");
		this.frill_left = body.getChild("frill_left");
		this.frill_right_extended = body.getChild("frill_right_extended");
		this.frill_left_extended = body.getChild("frill_left_extended");
		this.frill_right_extended.visible = false;
		this.frill_left_extended.visible = false;
		this.head_lower = body.getChild("head_lower");
		this.head_upper = body.getChild("head_upper");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-3.0F, -5.0F, -6.0F, 6.0F, 4.0F, 12.0F, new Dilation(0.0F)).uv(0, 14).uv(0, 20).cuboid(0.0F, -8.0F, -6.0F, 0.0F, 3.0F, 12.0F, new Dilation(0.01F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		body.addChild("tail", ModelPartBuilder.create().uv(0, 14).cuboid(0.0F, 9.0F, 6.0F, 0.0F, 8.0F, 6.0F, new Dilation(0.001F)).uv(0, 20), ModelTransform.pivot(0.0F, -18.0F, 0.0F));

		body.addChild("foot_bl", ModelPartBuilder.create().uv(33, 0).cuboid(0.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, -2.0F, -5.0F, 0.0F, 0.0F, 0.4363F));

		body.addChild("foot_fr", ModelPartBuilder.create().uv(33, 9).cuboid(-5.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -2.0F, -5.0F, 0.0F, 0.0F, -0.4363F));

		body.addChild("foot_br", ModelPartBuilder.create().uv(33, 6).cuboid(-5.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, -2.0F, 4.0F, 0.0F, 0.3054F, -0.4363F));

		body.addChild("foot_fl", ModelPartBuilder.create().uv(33, 3).cuboid(0.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, -2.0F, 4.0F, 0.0F, -0.3054F, 0.4363F));

		body.addChild("frill_right", ModelPartBuilder.create().uv(28, 0).cuboid(-4.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, 0.0F, -6.0F, 0.0F, 0.9163F, 0.0F));

		body.addChild("frill_left", ModelPartBuilder.create().uv(20, 0).cuboid(0.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, 0.0F, -6.0F, 0.0F, -0.9163F, 0.0F));

		body.addChild("frill_right_extended", ModelPartBuilder.create().uv(28, 8).cuboid(-4.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(-2.0F, 0.0F, -6.0F, 0.0F, 0.0F, 0.0F));

		body.addChild("frill_left_extended", ModelPartBuilder.create().uv(20, 8).cuboid(0.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new Dilation(0.001F)), ModelTransform.of(2.0F, 0.0F, -6.0F, 0.0F, 0.0F, 0.0F));

		body.addChild("head_lower", ModelPartBuilder.create().uv(0, 9).cuboid(-2.0F, 0.0F, -6.0F, 4.0F, 1.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, -6.0F, 0.0f, 0.0F, 0.0F));

		body.addChild("head_upper", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -3.0F, -6.0F, 4.0F, 3.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -3.0F, -6.0F, 0.0f, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
		FrilledBasiliskEntity frilledBasiliskEntity = ((FrilledBasiliskEntity)entity);
		if(frilledBasiliskEntity.isFlaring()) {
			this.head_upper.pitch = (float)(0.5f * MathHelper.cos((float)(0.0f * 0.9f + (float)Math.PI)));
			this.head_lower.pitch = (float)(0.5f * MathHelper.cos((float)(0.0f * 0.9f)));
			this.frill_left.visible = false;
			this.frill_right.visible = false;
			this.frill_left_extended.visible = true;
			this.frill_right_extended.visible = true;
		}
		else {
			this.head_upper.pitch = headPitch * ((float)Math.PI / 180);
			this.head_lower.pitch = headPitch * ((float)Math.PI / 180);
			this.frill_left.visible = true;
			this.frill_right.visible = true;
			this.frill_left_extended.visible = false;
			this.frill_right_extended.visible = false;
		}

		if(frilledBasiliskEntity.isSprinting()) {
			this.head_upper.pitch = (float)(0.5f * MathHelper.cos((float)(0.0f * 0.9f + (float)Math.PI)));
			this.head_lower.pitch = (float)(0.5f * MathHelper.cos((float)(0.0f * 0.9f + (float)Math.PI)));
			body.pitch = -0.35f;
		}
		else {
			body.pitch = 0.0F;
		}
		float new_yaw = (float)(0.3f * MathHelper.cos((float)(limbAngle * 0.6662f + (float)Math.PI)));
		this.foot_bl.yaw = new_yaw;
		this.foot_fr.yaw = new_yaw;
		new_yaw = (float)(0.3f * MathHelper.cos((float)(limbAngle * 0.6662f )));
		this.foot_br.yaw = new_yaw;
		this.foot_fl.yaw = new_yaw;
		this.tail.yaw = new_yaw;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
		if(this.child) {
            matrices.scale(0.4f, 0.4f, 0.4f);
			matrices.translate(0.0f, 2.25f, 0.0f);
        }
        ImmutableList.of(this.body).forEach(part -> part.render(matrices, vertices, light, overlay, red, green, blue, alpha));
        matrices.pop();
    }
}
