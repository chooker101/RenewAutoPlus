package net.renew_auto_plus;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class FallenEntityModel<T extends FallenEntity> extends EntityModel<T> {
    private static final String RIGHT_WING_1 = "right_wing1";
    private static final String RIGHT_WING_2 = "right_wing2";
    private static final String LEFT_WING_1 = "left_wing1";
    private static final String LEFT_WING_2 = "left_wing2";

	private final ModelPart rightWing1;
	private final ModelPart rightWing2;
	private final ModelPart leftWing1;
	private final ModelPart leftWing2;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart head;
	private final ModelPart body;

	public FallenEntityModel(ModelPart root) {
		this.rightWing1 = root.getChild(RIGHT_WING_1);
		this.rightWing2 = root.getChild(RIGHT_WING_2);
		this.leftWing1 = root.getChild(LEFT_WING_1);
		this.leftWing2 = root.getChild(LEFT_WING_2);
        this.rightArm = root.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = root.getChild(EntityModelPartNames.LEFT_ARM);
        this.head = root.getChild(EntityModelPartNames.HEAD);
		this.body = root.getChild(EntityModelPartNames.BODY);
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Primaries, Carpal, Secondaries, Ulna -- Maybe split like Phantom
        modelPartData.addChild(RIGHT_WING_1, ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -8.0F, 2.0F));
        ModelPartData temp_root = modelPartData.getChild(RIGHT_WING_1);

        temp_root.addChild("right_wing1_primaries", ModelPartBuilder.create().uv(0, 18).cuboid(0.5F, 2.5F, 0.0F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("right_wing1_carpal", -0.5F, 0.5F, 0.0F, 2.0F, 2.0F, 18.0F), ModelTransform.of(-14.0F, -11.0F, 6.0F, 0.2182F, -1.1345F, 0.0F));
        temp_root.addChild("right_wing1_secondaries", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("right_wing1_ulna", -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, -1.1345F, 0.0F));

        modelPartData.addChild(RIGHT_WING_2, ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -2.0F, 2.0F));
        temp_root = modelPartData.getChild(RIGHT_WING_2);

        temp_root.addChild("right_wing2_primaries", ModelPartBuilder.create().uv(0, 26).cuboid(0.0F, 1.5F, 0.1F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("right_wing2_carpal", -1.0F, -0.5F, 0.1F, 2.0F, 2.0F, 18.0F), ModelTransform.of(-13.0F, -5.0F, 13.0F, -0.3491F, -0.7854F, 0.0F));
        temp_root.addChild("right_wing2_secondaries", ModelPartBuilder.create().uv(0, 30).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("right_wing2_ulna", -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0873F, -0.7854F, 0.0F));

        modelPartData.addChild(LEFT_WING_1, ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -8.0F, 2.0F));
        temp_root = modelPartData.getChild(LEFT_WING_1);

        temp_root.addChild("left_wing1_primaries", ModelPartBuilder.create().uv(0, 18).cuboid(-0.5F, 2.5F, 0.0F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("left_wing1_carpal", -1.5F, 0.5F, 0.0F, 2.0F, 2.0F, 18.0F), ModelTransform.of(14.0F, -11.0F, 6.0F, 0.2182F, 1.1345F, 0.0F));
        temp_root.addChild("left_wing1_secondaries", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("left_wing1_ulna", -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.1345F, 0.0F));

        modelPartData.addChild(LEFT_WING_2, ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -2.0F, 2.0F));
        temp_root = modelPartData.getChild(LEFT_WING_2);

        temp_root.addChild("left_wing2_primaries", ModelPartBuilder.create().uv(0, 26).cuboid(0.0F, 1.5F, 0.1F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("left_wing2_carpal", -1.0F, -0.5F, 0.1F, 2.0F, 2.0F, 18.0F), ModelTransform.of(13.0F, -5.0F, 13.0F, -0.3491F, 0.7854F, 0.0F));
        temp_root.addChild("left_wing2_secondaries", ModelPartBuilder.create().uv(0, 30).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F).uv(22, 44).cuboid("left_wing2_ulna", -1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0873F, 0.7854F, 0.0F));
		
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -32.0F, -2.0F, 8.0F, 32.0F, 4.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(24, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.pivot(0.0F, -8.0F, 0.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(38, 18).cuboid(0.0F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F), ModelTransform.of(4.0F, -8.0F, 0.0F, 0.0F, 0.0F, -0.5236F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(24, 18).cuboid(-3.0F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F), ModelTransform.of(-4.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		return TexturedModelData.of(modelData, 64, 64);
	}

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
        float delta = age - (float)((FallenEntity)entity).age;
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.leftWing1.yaw = MathHelper.lerp(delta, (float)this.leftWing1.yaw, (float)(0.3f * MathHelper.cos((float)(limbAngle * 0.33333334f + (float)Math.PI))));
        this.rightWing2.yaw = MathHelper.lerp(delta, (float)this.rightWing2.yaw, (float)(0.3f * MathHelper.cos((float)(limbAngle * 0.33333334f + (float)Math.PI))));
        this.rightWing1.yaw = MathHelper.lerp(delta, (float)this.rightWing1.yaw, (float)(0.3f * MathHelper.cos((float)(limbAngle * 0.33333334f))));
        this.leftWing2.yaw = MathHelper.lerp(delta, (float)this.leftWing2.yaw, (float)(0.3f * MathHelper.cos((float)(limbAngle * 0.33333334f))));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        ImmutableList.of(this.rightWing1, this.rightWing2, this.leftWing1, this.leftWing2, this.body, this.rightArm, this.leftArm, this.head).forEach(part -> part.render(matrices, vertices, light, overlay, red, green, blue, alpha));
        matrices.pop();
    }
}
