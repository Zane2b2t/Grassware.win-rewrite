package me.zane.grassware.event.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public class BoundingBoxEvent extends Event
{

    private Block block;
    private BlockPos pos;
    private AxisAlignedBB boundingBox;
    private List<AxisAlignedBB> collidingBoxes;
    private Entity entity;

    public BoundingBoxEvent(Block block, BlockPos pos, AxisAlignedBB boundingBox, List<AxisAlignedBB> collidingBoxes, Entity entity) {
        this.block = block;
        this.pos = pos;
        this.boundingBox = boundingBox;
        this.collidingBoxes = collidingBoxes;
        this.entity = entity;
    }

    public Block getBlock() {
        return block;
    }

    public BlockPos getPos() {
        return pos;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public List<AxisAlignedBB> getCollidingBoxes() {
        return collidingBoxes;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

}