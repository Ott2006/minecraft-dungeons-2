package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Dartback;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class DartbackModel extends GeoModel<Dartback> {
    public DartbackModel(ModelPart root) {
        super(root, "dartback");
    }

    @Override
    protected void animate(Dartback entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart head = this.part("head");
        head.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.4F;
        head.xRot += headPitch * Mth.DEG_TO_RAD * 0.3F;
        float t = entity.getActionTime(ageInTicks - entity.tickCount);
        int action = entity.getAction();
        float walk = limbSwing;
        float amount = limbSwingAmount;
        if (action == Dartback.ACTION_DASH && t > 10) {
            walk = ageInTicks * 1.6F;
            amount = 1.0F;
        }
        // tripod gait
        for (int i = 0; i < 3; i++) {
            float phaseLeft = (i % 2 == 0) ? 0 : Mth.PI;
            float phaseRight = phaseLeft + Mth.PI;
            ModelPart left = this.part("leg_left_" + i);
            ModelPart right = this.part("leg_right_" + i);
            left.yRot += swing(walk, amount, 0.9F, 0.5F, phaseLeft);
            right.yRot += swing(walk, amount, 0.9F, 0.5F, phaseRight);
            left.zRot += Math.max(0, Mth.sin(walk * 0.9F + phaseLeft)) * amount * 0.35F;
            right.zRot -= Math.max(0, Mth.sin(walk * 0.9F + phaseRight)) * amount * 0.35F;
        }
        float raise = 0.0F;
        if (action == Dartback.ACTION_SPEW) {
            raise = t < 18 ? progress(t, 14) : 1.0F - progress(t - 36, 4);
        } else if (action == Dartback.ACTION_SUMMON) {
            raise = pulse(t, Dartback.SUMMON_TIME);
        }
        body.y -= raise * 3.0F;
        body.xRot -= raise * 0.35F;
        this.part("mandible_left").yRot += raise * 0.5F + Mth.sin(ageInTicks * 0.2F) * 0.05F;
        this.part("mandible_right").yRot -= raise * 0.5F + Mth.sin(ageInTicks * 0.2F) * 0.05F;
        this.part("spines").xRot -= raise * 0.1F;
        this.part("tail").xRot += Mth.sin(ageInTicks * 0.1F) * 0.06F + raise * 0.3F;
        if (action == Dartback.ACTION_DASH && t < 10) {
            body.xRot += progress(t, 8) * 0.15F;
        }
    }
}
