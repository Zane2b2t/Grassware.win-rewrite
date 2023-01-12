package us.velocity.client.impl.modules.render;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import us.velocity.client.api.module.Module;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.features.setting.impl.BooleanSetting;

public class BlockHighlight extends Module
{
    public static BlockHighlight blockHighlight = new BlockHighlight();

    public static final IntSetting alpha = register("Alpha", 90, 0, 255);
    public static final IntSetting linewidth = register("LineWidth", 1, 0, 5);
    public static final BooleanSetting depth = register("Depth", true);

    public BlockHighlight() {
   //     super("BlockHighlight", Category.RENDER, "Allows you to change how the block highlight behaves.");
 //      this.key = Keyboard.KEY_NONE;
    }

}
