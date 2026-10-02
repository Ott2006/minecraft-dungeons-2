package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Nester;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class NesterModel extends GeoModel<Nester> {
    public NesterModel(ModelPart root) {
        super(root, "nester");
    }

    @Override
    protected void animate(Nester entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart neck = this.part("neck");
        ModelPart head = this.part("head");
        neck.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.6F;
        neck.xRot += headPitch * Mth.DEG_TO_RAD * 0.5F;
        // gallop: front and back legs move in pairs
        float speed = 0.8F;
        this.part("leg_front_left").xRot = swing(limbSwing, limbSwingAmount, speed, 1.1F, 0);
        this.part("leg_front_right").xRot = swing(limbSwing, limbSwingAmount, speed, 1.1F, 0.5F);
        this.part("leg_back_left").xRot = swing(limbSwing, limbSwingAmount, speed, 1.1F, Mth.PI);
        this.part("leg_back_right").xRot = swing(limbSwing, limbSwingAmount, speed, 1.1F, Mth.PI + 0.5F);
        body.y += Mth.sin(limbSwing * speed * 2.0F) * limbSwingAmount * 1.0F;
        body.xRot += Mth.cos(limbSwing * speed) * limbSwingAmount * 0.06F;
        float bite = Mth.sin(this.attackTime * Mth.PI);
        this.part("jaw").xRot += 0.1F + bite * 0.8F + Mth.sin(ageInTicks * 0.1F) * 0.03F;
        neck.xRot += bite * 0.5F;
        head.xRot -= bite * 0.2F;
        this.part("tail").yRot = Mth.sin(ageInTicks * 0.15F) * 0.3F;
        this.part("tail").xRot -= limbSwingAmount * 0.3F;
    }
}
