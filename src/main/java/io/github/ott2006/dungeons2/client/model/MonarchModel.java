package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Monarch;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class MonarchModel extends GeoModel<Monarch> {
    public MonarchModel(ModelPart root) {
        // translucent, so that the spectral Monarch Echo can share the model
        super(root, "monarch", RenderType::entityTranslucent);
    }

    @Override
    protected void animate(Monarch entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart head = this.part("head");
        ModelPart armLeft = this.part("arm_left");
        ModelPart armRight = this.part("arm_right");
        ModelPart wingLeft = this.part("wing_left");
        ModelPart wingRight = this.part("wing_right");
        this.look(head, netHeadYaw, headPitch);
        this.part("leg_left").xRot = swing(limbSwing, limbSwingAmount, 0.4F, 0.6F, 0);
        this.part("leg_right").xRot = swing(limbSwing, limbSwingAmount, 0.4F, 0.6F, Mth.PI);
        armLeft.xRot = swing(limbSwing, limbSwingAmount, 0.4F, 0.4F, Mth.PI);
        armRight.xRot = swing(limbSwing, limbSwingAmount, 0.4F, 0.4F, 0);
        armLeft.zRot += 0.12F;
        armRight.zRot -= 0.12F;
        float flutter = Mth.sin(ageInTicks * 0.15F) * 0.15F;
        wingLeft.yRot += flutter;
        wingRight.yRot -= flutter;
        if (this.attackTime > 0) {
            float a = Mth.sin(this.attackTime * Mth.PI);
            armRight.xRot -= a * 1.6F;
            armLeft.xRot -= a * 1.6F;
        }
        float t = entity.getActionTime(ageInTicks - entity.tickCount);
        int action = entity.getAction();
        if (action == Monarch.ACTION_MULTI) {
            if (t < 20) {
                action = Monarch.ACTION_TWIRL;
            } else if (t < 45) {
                action = Monarch.ACTION_DASH;
                t -= 20;
            } else {
                action = Monarch.ACTION_SPIN;
                t -= 45;
            }
        }
        switch (action) {
            case Monarch.ACTION_SUMMON -> {
                float up = pulse(t, 30);
                armLeft.zRot += 2.4F * up;
                armRight.zRot -= 2.4F * up;
                wingLeft.yRot -= 0.7F * up;
                wingRight.yRot += 0.7F * up;
                head.xRot -= 0.5F * up;
            }
            case Monarch.ACTION_DASH -> {
                if (t < 10) {
                    body.xRot += 0.35F * progress(t, 8);
                    armLeft.xRot += 0.8F * progress(t, 8);
                    armRight.xRot += 0.8F * progress(t, 8);
                } else {
                    body.xRot += 0.5F;
                    armLeft.xRot = -1.4F;
                    armRight.xRot = -1.4F;
                    body.yRot += Mth.sin(t * 0.8F) * 0.4F;
                    wingLeft.yRot -= 0.5F;
                    wingRight.yRot += 0.5F;
                }
            }
            case Monarch.ACTION_SPIN -> {
                if (t < 40) {
                    body.yRot += t * 0.7F;
                    armLeft.zRot += 1.4F;
                    armRight.zRot -= 1.4F;
                    wingLeft.yRot -= 0.6F;
                    wingRight.yRot += 0.6F;
                }
            }
            case Monarch.ACTION_TWIRL -> {
                body.yRot += progress(t, 16) * Mth.TWO_PI;
                armLeft.zRot += 1.2F * pulse(t, 20);
                armRight.zRot -= 1.2F * pulse(t, 20);
            }
            default -> {
            }
        }
    }
}
