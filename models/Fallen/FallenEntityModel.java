// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class FallenEntityModel extends EntityModel<Entity> {
	private final ModelPart right_wing1;
	private final ModelPart right_wing2;
	private final ModelPart left_wing1;
	private final ModelPart left_wing2;
	private final ModelPart bb_main;
	public FallenEntityModel(ModelPart root) {
		this.right_wing1 = root.getChild("right_wing1");
		this.right_wing2 = root.getChild("right_wing2");
		this.left_wing1 = root.getChild("left_wing1");
		this.left_wing2 = root.getChild("left_wing2");
		this.bb_main = root.getChild("bb_main");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData right_wing1 = modelPartData.addChild("right_wing1", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -8.0F, 2.0F));

		ModelPartData right_wing1_primaries_r1 = right_wing1.addChild("right_wing1_primaries_r1", ModelPartBuilder.create().uv(0, 18).cuboid(0.5F, 2.5F, 0.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-0.5F, 0.5F, 0.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(-14.0F, -11.0F, 6.0F, 0.2182F, -1.1345F, 0.0F));

		ModelPartData right_wing1_secondaries_r1 = right_wing1.addChild("right_wing1_secondaries_r1", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, -1.1345F, 0.0F));

		ModelPartData right_wing2 = modelPartData.addChild("right_wing2", ModelPartBuilder.create(), ModelTransform.pivot(-3.0F, -2.0F, 2.0F));

		ModelPartData right_wing2_secondaries_r1 = right_wing2.addChild("right_wing2_secondaries_r1", ModelPartBuilder.create().uv(0, 30).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0873F, -0.7854F, 0.0F));

		ModelPartData right_wing2_primaries_r1 = right_wing2.addChild("right_wing2_primaries_r1", ModelPartBuilder.create().uv(0, 26).cuboid(0.0F, 1.5F, 0.1F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.0F, -0.5F, 0.1F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(-13.0F, -5.0F, 13.0F, -0.3491F, -0.7854F, 0.0F));

		ModelPartData left_wing1 = modelPartData.addChild("left_wing1", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -8.0F, 2.0F));

		ModelPartData left_wing1_primaries_r1 = left_wing1.addChild("left_wing1_primaries_r1", ModelPartBuilder.create().uv(0, 18).cuboid(-0.5F, 2.5F, 0.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.5F, 0.5F, 0.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(14.0F, -11.0F, 6.0F, 0.2182F, 1.1345F, 0.0F));

		ModelPartData left_wing1_secondaries_r1 = left_wing1.addChild("left_wing1_secondaries_r1", ModelPartBuilder.create().uv(0, 22).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.6109F, 1.1345F, 0.0F));

		ModelPartData left_wing2 = modelPartData.addChild("left_wing2", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, -2.0F, 2.0F));

		ModelPartData left_wing2_secondaries_r1 = left_wing2.addChild("left_wing2_secondaries_r1", ModelPartBuilder.create().uv(0, 30).cuboid(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 0.0F, 0.0873F, 0.7854F, 0.0F));

		ModelPartData left_wing2_primaries_r1 = left_wing2.addChild("left_wing2_primaries_r1", ModelPartBuilder.create().uv(0, 26).cuboid(0.0F, 1.5F, 0.1F, 0.0F, 4.0F, 18.0F, new Dilation(0.0F))
		.uv(24, 44).cuboid(-1.0F, -0.5F, 0.1F, 2.0F, 2.0F, 18.0F, new Dilation(0.0F)), ModelTransform.of(13.0F, -5.0F, 13.0F, -0.3491F, 0.7854F, 0.0F));

		ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -32.0F, -2.0F, 8.0F, 32.0F, 4.0F, new Dilation(0.0F))
		.uv(24, 0).cuboid(-4.0F, -42.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData left_arm_r1 = bb_main.addChild("left_arm_r1", ModelPartBuilder.create().uv(38, 18).cuboid(0.0F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -32.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		ModelPartData right_arm_r1 = bb_main.addChild("right_arm_r1", ModelPartBuilder.create().uv(24, 18).cuboid(-3.0F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -32.0F, 0.0F, 0.0F, 0.0F, 0.5236F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		right_wing1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		right_wing2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_wing1.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		left_wing2.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
		bb_main.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}