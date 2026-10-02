package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Harmonizer;
import io.github.ott2006.dungeons2.entity.Sprout;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/** Used by both the sprout and the harmonizer, its massive mini-boss variant. */
public class SproutModel<T extends Sprout> extends GeoModel<T> {
    public SproutModel(ModelPart root) {
        super(root, "sprout");
    }

    @Override
    protected void animate(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart bulb = this.part("bulb");
        bulb.y += Mth.sin(ageInTicks * 0.1F) * 1.0F;
        bulb.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.4F;
        bulb.xRot += headPitch * Mth.DEG_TO_RAD * 0.3F;
        this.part("crown").yRot = ageInTicks * 0.02F;
        float t = entity.getActionTime(ageInTicks - entity.tickCount);
        int action = entity.getAction();
        if (action == Sprout.ACTION_BEAM) {
            float flash = 1.0F + Mth.sin(ageInTicks * 1.2F) * 0.04F;
            bulb.xScale = flash;
            bulb.yScale = flash;
            bulb.zScale = flash;
        }
        for (int i = 0; i < 6; i++) {
            ModelPart tentacle = this.part("tentacle_" + i);
            float a = (i * 60 + 30) * Mth.DEG_TO_RAD;
            float flare = 0.25F + Mth.sin(ageInTicks * 0.12F + i * 1.1F) * 0.15F + Mth.sin(this.attackTime * Mth.PI) * 0.8F;
            if (action == Sprout.ACTION_BEAM) {
                flare = 0.05F + Mth.sin(ageInTicks * 0.8F + i) * 0.05F;
            } else if (action == Harmonizer.ACTION_ROAR) {
                flare += pulse(t, Harmonizer.ROAR_TIME) * 1.1F;
            }
            tentacle.xRot = flare * Mth.sin(a);
            tentacle.zRot = -flare * Mth.cos(a);
            if (entity.isAggressive()) {
                tentacle.xRot += Mth.sin(ageInTicks * 0.5F + i) * 0.1F;
            }
        }
        if (action == Harmonizer.ACTION_GRAB) {
            ModelPart grab = this.part("tentacle_4");
            grab.xRot = -2.4F * pulse(t, Harmonizer.GRAB_TIME);
            grab.zRot = 0.0F;
        }
    }
}
