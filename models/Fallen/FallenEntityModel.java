// Made with Blockbench 4.9.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class FallenEntityModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "fallenentitymodel"), "main");
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

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition right_wing1 = partdefinition.addOrReplaceChild("right_wing1", CubeListBuilder.create(), PartPose.offset(-17.0F, -15.0F, 8.0F));

		PartDefinition right_wing1_primaries_r1 = right_wing1.addOrReplaceChild("right_wing1_primaries_r1", CubeListBuilder.create().texOffs(0, 18).addBox(0.5F, 2.5F, 0.0F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-0.5F, 0.5F, 0.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.2182F, -1.1345F, 0.0F));

		PartDefinition right_wing1_secondaries_r1 = right_wing1.addOrReplaceChild("right_wing1_secondaries_r1", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, 7.0F, -6.0F, 0.6109F, -1.1345F, 0.0F));

		PartDefinition right_wing2 = partdefinition.addOrReplaceChild("right_wing2", CubeListBuilder.create(), PartPose.offset(-3.0F, -2.0F, 2.0F));

		PartDefinition right_wing2_secondaries_r1 = right_wing2.addOrReplaceChild("right_wing2_secondaries_r1", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0873F, -0.7854F, 0.0F));

		PartDefinition right_wing2_primaries_r1 = right_wing2.addOrReplaceChild("right_wing2_primaries_r1", CubeListBuilder.create().texOffs(0, 26).addBox(0.0F, 1.5F, 0.1F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.0F, -0.5F, 0.1F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.0F, -5.0F, 13.0F, -0.3491F, -0.7854F, 0.0F));

		PartDefinition left_wing1 = partdefinition.addOrReplaceChild("left_wing1", CubeListBuilder.create(), PartPose.offset(17.0F, -15.0F, 9.0F));

		PartDefinition left_wing1_primaries_r1 = left_wing1.addOrReplaceChild("left_wing1_primaries_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-0.5F, 2.5F, 0.0F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.5F, 0.5F, 0.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, -1.0F, 0.2182F, 1.1345F, 0.0F));

		PartDefinition left_wing1_secondaries_r1 = left_wing1.addOrReplaceChild("left_wing1_secondaries_r1", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-14.0F, 7.0F, -7.0F, 0.6109F, 1.1345F, 0.0F));

		PartDefinition left_wing2 = partdefinition.addOrReplaceChild("left_wing2", CubeListBuilder.create(), PartPose.offset(3.0F, -2.0F, 2.0F));

		PartDefinition left_wing2_secondaries_r1 = left_wing2.addOrReplaceChild("left_wing2_secondaries_r1", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, 2.0F, 0.0F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.0873F, 0.7854F, 0.0F));

		PartDefinition left_wing2_primaries_r1 = left_wing2.addOrReplaceChild("left_wing2_primaries_r1", CubeListBuilder.create().texOffs(0, 26).addBox(0.0F, 1.5F, 0.1F, 0.0F, 4.0F, 18.0F, new CubeDeformation(0.0F))
		.texOffs(24, 44).addBox(-1.0F, -0.5F, 0.1F, 2.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.0F, -5.0F, 13.0F, -0.3491F, 0.7854F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -32.0F, -2.0F, 8.0F, 32.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).addBox(-4.0F, -42.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition left_arm_r1 = bb_main.addOrReplaceChild("left_arm_r1", CubeListBuilder.create().texOffs(38, 18).addBox(0.0F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -32.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition right_arm_r1 = bb_main.addOrReplaceChild("right_arm_r1", CubeListBuilder.create().texOffs(24, 18).addBox(-3.0F, 0.0F, -2.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -32.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		right_wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_wing2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_wing2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}