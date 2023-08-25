package me.zane.grassware.event.events;

import me.zane.grassware.event.bus.Event;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
public class BreakBlockEvent extends Event {
    private final BlockPos pos;
    private final EnumFacing enumFacing;

    public BreakBlockEvent(BlockPos pos, EnumFacing enumFacing) {
        this.pos = pos;
        this.enumFacing = enumFacing;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getEnumFacing() {
        return enumFacing;
    }
}
