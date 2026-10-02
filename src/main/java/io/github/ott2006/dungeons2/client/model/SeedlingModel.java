package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Seedling;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class SeedlingModel extends GeoModel<Seedling> {
    public SeedlingModel(ModelPart root) {
        super(root, "seedling");
    }

    @Override
    protected void animate(Seedling entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        body.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.4F;
        body.y += Mth.sin(ageInTicks * 0.25F) * 0.3F - Math.abs(Mth.sin(limbSwing * 1.2F)) * limbSwingAmount * 1.2F;
        float open = 0.15F + Mth.sin(ageInTicks * 0.1F) * 0.05F + Mth.sin(this.attackTime * Mth.PI) * 0.9F;
        if (entity.isAggressive()) {
            open += 0.15F + Mth.sin(ageInTicks * 0.6F) * 0.1F;
        }
        this.part("upper_jaw").xRot -= open * 0.5F;
        this.part("lower_jaw").xRot += open;
        this.part("leaf_left").zRot += Mth.sin(ageInTicks * 0.1F) * 0.12F;
        this.part("leaf_right").zRot -= Mth.sin(ageInTicks * 0.1F + 0.6F) * 0.12F;
        String[] legs = {"leg_front_left", "leg_front_right", "leg_back_left", "leg_back_right"};
        for (int i = 0; i < legs.length; i++) {
            this.part(legs[i]).xRot = swing(limbSwing, limbSwingAmount, 1.2F, 1.2F, (i == 0 || i == 3) ? 0 : Mth.PI);
        }
    }
}
