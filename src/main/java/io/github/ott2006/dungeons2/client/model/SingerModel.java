package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Singer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class SingerModel extends GeoModel<Singer> {
    public SingerModel(ModelPart root) {
        super(root, "singer");
    }

    @Override
    protected void animate(Singer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart neck = this.part("neck");
        ModelPart head = this.part("head");
        ModelPart armLeft = this.part("arm_left");
        ModelPart armRight = this.part("arm_right");
        this.part("leg_left").xRot = swing(limbSwing, limbSwingAmount, 0.5F, 0.7F, 0);
        this.part("leg_right").xRot = swing(limbSwing, limbSwingAmount, 0.5F, 0.7F, Mth.PI);
        armLeft.xRot = swing(limbSwing, limbSwingAmount, 0.5F, 0.5F, Mth.PI);
        armRight.xRot = swing(limbSwing, limbSwingAmount, 0.5F, 0.5F, 0);
        armLeft.zRot += Mth.sin(ageInTicks * 0.05F) * 0.05F + 0.05F;
        armRight.zRot -= Mth.sin(ageInTicks * 0.05F) * 0.05F + 0.05F;
        neck.xRot += Mth.sin(ageInTicks * 0.04F) * 0.06F;
        neck.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
        head.yRot += netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
        head.xRot += headPitch * Mth.DEG_TO_RAD * 0.5F;
        if (entity.isSinging()) {
            float sway = Mth.sin(ageInTicks * 0.15F);
            neck.xRot -= 0.55F + sway * 0.05F;
            head.xRot -= 0.35F;
            head.zRot += sway * 0.12F;
            armLeft.zRot -= 0.5F + Mth.sin(ageInTicks * 0.2F) * 0.15F;
            armRight.zRot += 0.5F + Mth.sin(ageInTicks * 0.2F) * 0.15F;
            armLeft.xRot -= 0.4F;
            armRight.xRot -= 0.4F;
            this.part("body").zRot += sway * 0.04F;
        }
    }
}
