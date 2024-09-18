// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class wastes_frilled_basilisk<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "wastes_frilled_basilisk"), "main");
	private final ModelPart base;

	public wastes_frilled_basilisk(ModelPart root) {
		this.base = root.getChild("base");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -5.0F, -6.0F, 6.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(0.0F, -9.0F, 6.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 20).addBox(0.0F, -8.0F, -6.0F, 0.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition foot_fr_r1 = base.addOrReplaceChild("foot_fr_r1", CubeListBuilder.create().texOffs(33, 9).addBox(-5.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, -5.0F, 0.0F, 0.0F, -0.4363F));

		PartDefinition foot_br_r1 = base.addOrReplaceChild("foot_br_r1", CubeListBuilder.create().texOffs(33, 6).addBox(-5.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -2.0F, 4.0F, 0.0F, 0.3054F, -0.4363F));

		PartDefinition foot_fl_r1 = base.addOrReplaceChild("foot_fl_r1", CubeListBuilder.create().texOffs(33, 0).addBox(0.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -2.0F, -5.0F, 0.0F, 0.0F, 0.4363F));

		PartDefinition foot_bl_r1 = base.addOrReplaceChild("foot_bl_r1", CubeListBuilder.create().texOffs(33, 3).addBox(0.0F, 0.0F, -1.0F, 5.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -2.0F, 4.0F, 0.0F, -0.3054F, 0.4363F));

		PartDefinition frill_right_r1 = base.addOrReplaceChild("frill_right_r1", CubeListBuilder.create().texOffs(28, 0).addBox(-4.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -6.0F, 0.0F, 0.9163F, 0.0F));

		PartDefinition frill_left_r1 = base.addOrReplaceChild("frill_left_r1", CubeListBuilder.create().texOffs(20, 0).addBox(0.0F, -8.0F, 0.0F, 4.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -6.0F, 0.0F, -0.9163F, 0.0F));

		PartDefinition head_lower_r1 = base.addOrReplaceChild("head_lower_r1", CubeListBuilder.create().texOffs(0, 9).addBox(-2.0F, 0.0F, -6.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -6.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition head_upper_r1 = base.addOrReplaceChild("head_upper_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -6.0F, -0.1309F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}