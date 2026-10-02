package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.EchoGolem;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class EchoGolemModel extends GeoModel<EchoGolem> {
    public EchoGolemModel(ModelPart root) {
        super(root, "echo_golem");
    }

    @Override
    protected void animate(EchoGolem entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart head = this.part("head");
        ModelPart armLeft = this.part("arm_left");
        ModelPart armRight = this.part("arm_right");
        if (entity.isDormant()) {
            head.xRot += 0.75F;
            body.xRot += 0.2F;
            armLeft.xRot -= 0.2F;
            armRight.xRot -= 0.2F;
            return;
        }
        this.part("crystal").yRot = ageInTicks * 0.05F;
        this.look(head, netHeadYaw, headPitch);
        this.part("leg_left").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 1.0F, 0);
        this.part("leg_right").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 1.0F, Mth.PI);
        armLeft.xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 0.8F, Mth.PI);
        armRight.xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 0.8F, 0);
        if (entity.isInSittingPose()) {
            this.part("leg_left").xRot = -1.4F;
            this.part("leg_right").xRot = -1.4F;
            body.y += 6.0F;
            this.part("leg_left").y += 6.0F;
            this.part("leg_right").y += 6.0F;
        }
        if (this.attackTime > 0) {
            float a = Mth.sin(this.attackTime * Mth.PI);
            armLeft.xRot -= a * 2.0F;
            armRight.xRot -= a * 2.0F;
        }
        if (entity.isDancing()) {
            float beat = ageInTicks * 0.45F;
            body.zRot += Mth.sin(beat) * 0.15F;
            head.yRot += Mth.sin(beat) * 0.35F;
            armLeft.xRot = -2.6F + Mth.sin(beat * 1.3F) * 0.3F;
            armRight.xRot = -2.6F - Mth.sin(beat * 1.3F) * 0.3F;
            this.part("leg_left").xRot = Mth.sin(beat) * 0.3F;
            this.part("leg_right").xRot = -Mth.sin(beat) * 0.3F;
        }
    }
}
