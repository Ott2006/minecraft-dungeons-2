package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Blub;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class BlubModel extends GeoModel<Blub> {
    public BlubModel(ModelPart root) {
        super(root, "blub");
    }

    @Override
    protected void animate(Blub entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart head = this.part("head");
        this.look(head, netHeadYaw * 0.6F, headPitch * 0.6F);
        float hop = Math.abs(Mth.sin(limbSwing * 0.6F)) * limbSwingAmount;
        body.y -= hop * 3.0F;
        // soft and squishy
        float squish = 1.0F + Mth.sin(ageInTicks * 0.12F) * 0.025F - hop * 0.06F;
        body.yScale = squish;
        body.xScale = 2.0F - squish;
        body.zScale = 2.0F - squish;
        this.part("ear_left").zRot += Mth.sin(ageInTicks * 0.08F) * 0.08F;
        this.part("ear_right").zRot -= Mth.sin(ageInTicks * 0.08F + 1.0F) * 0.08F;
        this.part("ear_left").xRot -= hop * 0.7F;
        this.part("ear_right").xRot -= hop * 0.7F;
        this.part("tail").yRot = Mth.sin(ageInTicks * 0.3F) * 0.25F;
        String[] legs = {"leg_front_left", "leg_front_right", "leg_back_left", "leg_back_right"};
        for (int i = 0; i < legs.length; i++) {
            ModelPart leg = this.part(legs[i]);
            leg.xRot = swing(limbSwing, limbSwingAmount, 0.6F, 1.0F, i < 2 ? 0 : Mth.PI);
            leg.y -= hop * 2.0F;
        }
    }
}
