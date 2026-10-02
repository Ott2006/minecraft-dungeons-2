package io.github.ott2006.dungeons2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ott2006.dungeons2.Dungeons2;
import io.github.ott2006.dungeons2.entity.SiftMonster;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

/** Enraged sifters glow with a pulsing red light. */
public class EnragedLayer<T extends SiftMonster, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation WHITE = Dungeons2.id("textures/entity/white.png");

    public EnragedLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isEnraged() || entity.isInvisible()) {
            return;
        }
        // additive, so the glow also shows on dark sculk skin
        float pulse = 0.55F + 0.25F * Mth.sin(ageInTicks * 0.3F);
        int color = FastColor.ARGB32.color(255, (int) (200 * pulse), (int) (25 * pulse), (int) (20 * pulse));
        this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(RenderType.eyes(WHITE)),
                15728640, OverlayTexture.NO_OVERLAY, color);
    }
}
