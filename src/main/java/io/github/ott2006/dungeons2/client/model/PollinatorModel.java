package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Pollinator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class PollinatorModel extends GeoModel<Pollinator> {
    public PollinatorModel(ModelPart root) {
        super(root, "pollinator");
    }

    @Override
    protected void animate(Pollinator entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart head = this.part("head");
        ModelPart throat = this.part("throat");
        ModelPart tongue = this.part("tongue");
        head.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
        float t = entity.getActionTime(ageInTicks - entity.tickCount);
        float puff = entity.getAction() == Pollinator.ACTION_PUFF ? progress(t, Pollinator.PUFF_TIME - 2) : 0.0F;
        if (entity.getAction() == Pollinator.ACTION_PUFF && t > Pollinator.PUFF_TIME - 2) {
            puff = 0.0F;
        }
        float breath = 1.0F + Mth.sin(ageInTicks * 0.15F) * 0.08F + puff * 0.9F;
        throat.xScale = breath;
        throat.yScale = breath;
        throat.zScale = breath;
        head.xRot -= puff * 0.35F;
        boolean tongueOut = entity.getAction() == Pollinator.ACTION_TONGUE;
        tongue.visible = tongueOut;
        if (tongueOut) {
            tongue.zScale = 0.2F + pulse(t, Pollinator.TONGUE_TIME) * 1.4F;
            head.xRot += 0.15F;
        }
        float hop = limbSwingAmount * Math.abs(Mth.sin(limbSwing * 0.5F));
        this.part("body").y -= hop * 2.5F;
        this.part("leg_back_left").xRot = swing(limbSwing, limbSwingAmount, 0.5F, 1.2F, 0);
        this.part("leg_back_right").xRot = swing(limbSwing, limbSwingAmount, 0.5F, 1.2F, 0);
        this.part("leg_front_left").xRot = swing(limbSwing, limbSwingAmount, 0.5F, 0.8F, Mth.PI);
        this.part("leg_front_right").xRot = swing(limbSwing, limbSwingAmount, 0.5F, 0.8F, Mth.PI);
        for (String leg : new String[]{"leg_back_left", "leg_back_right", "leg_front_left", "leg_front_right"}) {
            this.part(leg).y -= hop * 2.5F;
        }
    }
}
