package io.github.ott2006.dungeons2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** A mob renderer with a texture chosen per entity and an optional size scale. */
public class GeoMobRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    private final Function<T, ResourceLocation> texture;
    private final float scale;

    public GeoMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius, float scale,
                          Function<T, ResourceLocation> texture) {
        super(context, model, shadowRadius * scale);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture.apply(entity);
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        float s = this.scale * (entity.isBaby() ? 0.55F : 1.0F);
        if (s != 1.0F) {
            poseStack.scale(s, s, s);
        }
    }
}
