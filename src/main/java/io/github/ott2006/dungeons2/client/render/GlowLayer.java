package io.github.ott2006.dungeons2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/** Renders the emissive "_glow" texture of a mob at full brightness. The function may return null to skip it. */
public class GlowLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final Function<T, ResourceLocation> glowTexture;

    public GlowLayer(RenderLayerParent<T, M> parent, Function<T, ResourceLocation> glowTexture) {
        super(parent);
        this.glowTexture = glowTexture;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = this.glowTexture.apply(entity);
        if (texture == null || entity.isInvisible()) {
            return;
        }
        this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(RenderType.eyes(texture)), 15728640, OverlayTexture.NO_OVERLAY);
    }
}
