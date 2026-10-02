package io.github.ott2006.dungeons2.client.model;

import io.github.ott2006.dungeons2.client.geo.GeoModel;
import io.github.ott2006.dungeons2.entity.Sentinel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class SentinelModel extends GeoModel<Sentinel> {
    public SentinelModel(ModelPart root) {
        super(root, "sentinel");
    }

    @Override
    protected void animate(Sentinel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        ModelPart body = this.part("body");
        ModelPart head = this.part("head");
        this.look(head, netHeadYaw, headPitch);
        this.part("leg_left").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 1.0F, 0);
        this.part("leg_right").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 1.0F, Mth.PI);
        this.part("arm_left").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 0.6F, Mth.PI);
        this.part("arm_right").xRot = swing(limbSwing, limbSwingAmount, 0.6662F, 0.6F, 0);
        this.part("crest").zRot += Mth.sin(ageInTicks * 0.2F) * 0.05F;
        float t = entity.getActionTime(ageInTicks - entity.tickCount);
        switch (entity.getAction()) {
            case Sentinel.ACTION_VOLLEY -> {
                head.yRot += Mth.sin(t * 1.3F) * 0.6F * pulse(t, Sentinel.VOLLEY_TIME);
                body.xRot += 0.15F * pulse(t, Sentinel.VOLLEY_TIME);
                this.part("chin").xRot -= 0.4F * pulse(t, Sentinel.VOLLEY_TIME);
            }
            case Sentinel.ACTION_EXHAUSTED -> {
                float slump = Math.min(1.0F, t / 8.0F);
                head.xRot += 0.6F * slump;
                body.xRot += 0.3F * slump;
                this.part("arm_left").xRot += 0.3F * slump;
                this.part("arm_right").xRot += 0.3F * slump;
            }
            default -> {
            }
        }
    }
}
