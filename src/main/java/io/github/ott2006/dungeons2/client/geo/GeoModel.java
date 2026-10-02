package io.github.ott2006.dungeons2.client.geo;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/**
 * Base class of the mod's entity models. Bones are looked up by name from the {@link GeoDefinition}; subclasses
 * only implement the animation.
 */
public abstract class GeoModel<T extends Entity> extends HierarchicalModel<T> {
    private static final ModelPart EMPTY = new ModelPart(java.util.List.of(), Map.of());
    private final ModelPart root;
    private final Map<String, ModelPart> parts;

    protected GeoModel(ModelPart root, String geo) {
        this(root, geo, net.minecraft.client.renderer.RenderType::entityCutoutNoCull);
    }

    protected GeoModel(ModelPart root, String geo, Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
        this.root = root;
        this.parts = GeoDefinition.get(geo).resolve(root);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    /** Returns the bone with the given name, or an invisible dummy part if the model has no such bone. */
    protected ModelPart part(String name) {
        return this.parts.getOrDefault(name, EMPTY);
    }

    @Override
    public final void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    protected abstract void animate(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    // ------------------------------------------------------------------ helpers
    protected void look(ModelPart head, float netHeadYaw, float headPitch) {
        head.yRot += netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot += headPitch * Mth.DEG_TO_RAD;
    }

    protected static float swing(float limbSwing, float limbSwingAmount, float speed, float amount, float phase) {
        return Mth.cos(limbSwing * speed + phase) * amount * limbSwingAmount;
    }

    /** 0 → 1 → 0 over the duration of an action. */
    protected static float pulse(float time, float duration) {
        if (time < 0 || time > duration) {
            return 0;
        }
        return Mth.sin(time / duration * Mth.PI);
    }

    /** 0 → 1, clamped. */
    protected static float progress(float time, float duration) {
        return Mth.clamp(time / duration, 0.0F, 1.0F);
    }
}
