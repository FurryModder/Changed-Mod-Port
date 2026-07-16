package net.changed.mixin.compatibility.CarryOn;

/*@Mixin(value = RenderEvents.class, remap = false)
@RequiredMods("carryon")
public abstract class RenderEventsMixin {
    @Unique private static Player currentPlayer = null;

    @Shadow @NotNull private static PlayerRenderer getRenderPlayer(AbstractClientPlayer player) { return null; }

    private static final Pair<ModelPart, ResourceLocation> NULL_PART = Pair.of(new ModelPart(List.of(), Map.of()), null);
    private Optional<Pair<ModelPart, ResourceLocation>> remapHumanoidPart(ModelPart part) {
        PlayerModel<AbstractClientPlayer> playerModel = getRenderPlayer((AbstractClientPlayer) currentPlayer).getModel();

        return Optional.ofNullable(ProcessTransfur.ifPlayerTransfurred(currentPlayer, variant -> {
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(variant.getChangedEntity());
            if (renderer instanceof AdvancedHumanoidRenderer<?,?> advanced) {
                var changedModel = advanced.getModel(variant.getChangedEntity());

                if (part == playerModel.rightArm)
                    return Pair.of(changedModel.getArm(HumanoidArm.RIGHT), renderer.getTextureLocation(variant.getChangedEntity()));
                else if (part == playerModel.leftArm)
                    return Pair.of(changedModel.getArm(HumanoidArm.LEFT), renderer.getTextureLocation(variant.getChangedEntity()));
                else if (part == playerModel.rightSleeve)
                    return NULL_PART;
                else if (part == playerModel.leftSleeve)
                    return NULL_PART;
                else
                    return null;
            } else {
                return null;
            }
        }));
    }

    @Inject(method = "drawArms", at = @At("HEAD"))
    public void copyPlayerForLater(Player player, float partialticks, PoseStack matrix, MultiBufferSource buffer, int light, CallbackInfo ci) {
        currentPlayer = player;
    }

    @Inject(method = "onEvent", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/event/RenderPlayerEvent$Pre;getPlayer()Lnet/minecraft/world/entity/player/Player;"))
    public void copyPlayerForLater(RenderPlayerEvent.Pre event, CallbackInfo ci) {
        currentPlayer = event.getPlayer();
    }

    @Inject(method = "renderArmPre", at = @At("HEAD"), cancellable = true)
    public void renderChangedArmPre(ModelPart arm, CallbackInfo ci) {
        remapHumanoidPart(arm).ifPresent(pair -> {
            ci.cancel();
        });
    }

    @Inject(method = "renderArmPost", at = @At("HEAD"), cancellable = true)
    public void renderChangedArmPost(ModelPart arm, float x, float z, boolean right, boolean sneaking, int light, PoseStack matrix, VertexConsumer builder, CallbackInfo ci) {
        remapHumanoidPart(arm).ifPresent(pair -> {
            ci.cancel();
        });
    }

    @Redirect(method = "onRenderLevel", at = @At(value = "INVOKE", target = "Ltschipp/carryon/client/helper/CarryRenderHelper;renderItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/resources/model/BakedModel;)V"))
    public void maybeOffsetItem(BlockState state, CompoundTag tag, ItemStack stack, ItemStack tileStack, PoseStack matrix, MultiBufferSource buffer, int light, BakedModel model) {
        ProcessTransfur.ifPlayerTransfurred(currentPlayer, variant -> {
            matrix.pushPose(); // reset matrix to entity
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(variant.getChangedEntity());
            if (renderer instanceof AdvancedHumanoidRenderer<?,?> advanced) {
                int perspective = CarryRenderHelper.getPerspective();
                var entity = variant.getChangedEntity();
                AdvancedHumanoidModel entityModel = advanced.getModel(entity);
                float partialTicks = Minecraft.getInstance().getDeltaFrameTime();

                boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
                float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
                float f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
                float netHeadYaw = f1 - f;
                if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
                    LivingEntity livingentity = (LivingEntity)entity.getVehicle();
                    f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
                    netHeadYaw = f1 - f;
                    float f3 = Mth.wrapDegrees(netHeadYaw);
                    if (f3 < -85.0F) {
                        f3 = -85.0F;
                    }

                    if (f3 >= 85.0F) {
                        f3 = 85.0F;
                    }

                    f = f1 - f3;
                    if (f3 * f3 > 2500.0F) {
                        f += f3 * 0.2F;
                    }

                    netHeadYaw = f1 - f;
                }

                float headPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
                if (LivingEntityRenderer.isEntityUpsideDown(entity)) {
                    headPitch *= -1.0F;
                    netHeadYaw *= -1.0F;
                }

                float limbSwing = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);
                float limbSwingAmount = Math.min(Mth.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed), 1.0F);

                // Setup animations for the entity
                entityModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                entityModel.setupAnim(entity, limbSwing, limbSwingAmount, entity.tickCount + partialTicks, netHeadYaw, headPitch);
                var torso = entityModel.getTorso();

                if (entityModel instanceof AdvancedHumanoidModelInterface modelInterface)
                    matrix.translate(0.0, 0.0, (perspective == 2 ? -1 : 1) * modelInterface.getAnimator(entity).forwardOffset / 8.0D);

                if (torso != null) {
                    float oldY = torso.y;
                    torso.y = -torso.y;
                    torso.translateAndRotate(matrix);
                    torso.y = oldY;
                }
            }
            CarryRenderHelper.renderItem(state, tag, stack, tileStack, matrix, buffer, light, model);
            matrix.popPose();
        }, () -> {
            CarryRenderHelper.renderItem(state, tag, stack, tileStack, matrix, buffer, light, model);
        });
    }
}*/
