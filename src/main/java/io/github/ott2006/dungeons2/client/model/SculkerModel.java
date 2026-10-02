package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Sculker;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/** Shared by hunters, scavengers, stalkers and trappers. */
public class SculkerModel extends GeoModel<Sculker> {
    public SculkerModel(ModelPart root) {
        super(root, "sculker");
    }

    @Override
    protected void animate(Sculker entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart head = this.part("head");
        ModelPart armLeft = this.part("arm_left");
        ModelPart armRight = this.part("arm_right");
        this.look(head, netHeadYaw, headPitch);
        this.part("leg_left").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 1.0F, 0);
        this.part("leg_right").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 1.0F, Mth.PI);
        armLeft.xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 0.6F, Mth.PI) - 0.2F;
        armRight.xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 0.6F, 0) - 0.2F;
        armLeft.zRot += 0.1F + Mth.sin(ageInTicks * 0.07F) * 0.05F;
        armRight.zRot -= 0.1F + Mth.sin(ageInTicks * 0.07F) * 0.05F;
        this.part("horn_left").zRot -= Mth.sin(ageInTicks * 0.1F) * 0.08F;
        this.part("horn_right").zRot += Mth.sin(ageInTicks * 0.1F) * 0.08F;
        if (this.attackTime > 0) {
            float a = Mth.sin(this.attackTime * Mth.PI);
            armRight.xRot -= a * 1.8F;
            armRight.yRot += a * 0.5F;
        }
        float t = entity.getActionTime(ageInTicks - entity.tickCount);
        switch (entity.getAction()) {
            case Sculker.ACTION_GRAB -> {
                float reach = pulse(t, Sculker.GRAB_TIME);
                armRight.xRot = -1.6F * reach;
                armRight.zRot = 0;
                armRight.yScale = 1.0F + reach * 0.6F;
            }
            case Sculker.ACTION_THROW -> {
                float up = t < 20 ? progress(t, 8) : 1.0F - progress(t - 20, 6);
                armLeft.xRot = -2.6F * up;
                armRight.xRot = -2.6F * up;
                armLeft.zRot = 0.3F * up;
                armRight.zRot = -0.3F * up;
            }
            case Sculker.ACTION_DODGE -> body.xRot -= 0.5F * pulse(t, Sculker.DODGE_TIME);
            case Sculker.ACTION_SLASH -> {
                armLeft.xRot -= 1.9F * pulse(t - 2, 8);
                armLeft.yRot -= 0.6F * pulse(t - 2, 8);
                armRight.xRot -= 1.9F * pulse(t - 8, 8);
                armRight.yRot += 0.6F * pulse(t - 8, 8);
                if (t > 13 && t < 22) {
                    body.yRot += progress(t - 13, 8) * Mth.TWO_PI;
                    armLeft.zRot += 1.3F;
                    armRight.zRot -= 1.3F;
                }
            }
            case Sculker.ACTION_LUNGE -> {
                if (t < 8) {
                    float back = progress(t, 7);
                    armLeft.xRot += 0.9F * back;
                    armRight.xRot += 0.9F * back;
                    body.xRot -= 0.2F * back;
                } else {
                    armLeft.xRot = -1.6F;
                    armRight.xRot = -1.6F;
                    body.xRot += 0.3F;
                }
            }
            case Sculker.ACTION_CAST -> {
                float up = progress(t, 10);
                armLeft.zRot += 2.0F * up;
                armRight.zRot -= 2.0F * up;
                armLeft.xRot -= 0.4F * up;
                armRight.xRot -= 0.4F * up;
                head.xRot -= 0.3F * up;
            }
            default -> {
            }
        }
    }
}
