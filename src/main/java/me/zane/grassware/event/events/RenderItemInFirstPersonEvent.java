package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RenderItemInFirstPersonEvent extends Event {

    public EntityLivingBase entity;
    public ItemStack stack;
    public ItemCameraTransforms.TransformType transformType;
    public boolean leftHanded;

    public RenderItemInFirstPersonEvent(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, int stage) {
        //super(stage);

        entity = entitylivingbaseIn;
        stack = heldStack;
        transformType = transform;
        this.leftHanded = leftHanded;
    }

    public ItemCameraTransforms.TransformType getTransformType() {
        return transformType;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}
