package me.zane.grassware.event.events;

import me.zane.grassware.event.EventStage;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RenderLivingEntityEvent
        extends EventStage {
    private final /* synthetic */ float limbSwingAmount;
    private final /* synthetic */ float limbSwing;
    private final /* synthetic */ float headPitch;
    private final /* synthetic */ ModelBase modelBase;
    private final /* synthetic */ float ageInTicks;
    private final /* synthetic */ float scaleFactor;
    private final /* synthetic */ EntityLivingBase entityLivingBase;
    private final /* synthetic */ float netHeadYaw;

    public RenderLivingEntityEvent(ModelBase modelBase, EntityLivingBase entityLivingBase, float f, float f2, float f3, float f4, float f5, float f6) {
        this.modelBase = modelBase;
        this.entityLivingBase = entityLivingBase;
        this.limbSwing = f;
        this.limbSwingAmount = f2;
        this.ageInTicks = f3;
        this.netHeadYaw = f4;
        this.headPitch = f5;
        this.scaleFactor = f6;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public float getHeadPitch() {
        return this.headPitch;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public EntityLivingBase getEntityLivingBase() {
        return this.entityLivingBase;
    }

    public ModelBase getModelBase() {
        return this.modelBase;
    }

    public float getNetHeadYaw() {
        return this.netHeadYaw;
    }

    public float getScaleFactor() {
        return this.scaleFactor;
    }

    public float getAgeInTicks() {
        return this.ageInTicks;
    }
}
