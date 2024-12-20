package me.zane.grassware.features.modules.render;
//i don't remember this working, and i don't know why
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.Render3DEvent;
import me.zane.grassware.event.events.UpdateEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.shader.impl.GradientShader;
import me.zane.grassware.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;


public class BurrowESP extends Module {
    BlockPos burrowPos;
    BlockPos burrowPos1;
    ArrayList<EntityPlayer> burrowedPlayers = new ArrayList<>();
    private final FloatSetting opacity = register("Opacity", 90.0f, 0.0f, 255.0f);
    private final BooleanSetting self = register("Self", false);
    private final BooleanSetting box = register("Box", false);
    private final BooleanSetting outline = register("Outline", true);

    @EventListener
    public void onUpdate(UpdateEvent event) {
        for (EntityPlayer players : mc.world.playerEntities) {
            if (!self.getValue() && players.equals(mc.player)) continue;
            burrowPos = new BlockPos(Math.floor(players.posX), Math.floor(players.posY+0.2), Math.floor(players.posZ));

            if (!(mc.world.getBlockState(burrowPos).getBlock() == Blocks.AIR)) continue;
            if (!burrowedPlayers.contains(players)) {
                burrowPos1 = new BlockPos(Math.floor(players.posX), Math.floor(players.posY+0.2), Math.floor(players.posZ));

                burrowedPlayers.add(players);
            }
        }

        burrowedPlayers.removeIf(players ->
                !mc.world.playerEntities.contains(players) ||
                        mc.world.getBlockState(players.getPosition()).getBlock() == Blocks.AIR
        );
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        for (EntityPlayer players : burrowedPlayers) {
            GradientShader.setup(opacity.getValue());

            if (box.getValue())
                RenderUtil.boxShader(burrowPos1);

            if (outline.getValue())
                RenderUtil.outlineShader(burrowPos1);

            GradientShader.finish();
        }
    }

}
