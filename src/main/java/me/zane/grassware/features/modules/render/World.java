package me.zane.grassware.features.modules.render;

import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.*;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
import me.zane.grassware.event.events.ChunkLoadEvent;
import me.zane.grassware.event.events.PacketEvent;
import me.zane.grassware.event.events.ParticleEvent;
import me.zane.grassware.event.events.Render3DEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.server.SPacketEffect;

public class World extends Module {
    private final BooleanSetting customFov = register("Custom Fov", true);
    private final FloatSetting fov = register("Fov", 130.0f, 50.0f, 150.0f).invokeVisibility(z -> customFov.getValue());
    private final BooleanSetting noDynamicFOV = register("No Dynamic FOV", true);
    private final BooleanSetting clearWeather = register("Clear Weather", true);
    private final BooleanSetting customTime = register("Custom Time", false);
    private final IntSetting time = register("Time", 10000, 0, 24000).invokeVisibility(z -> customTime.getValue());
    private final BooleanSetting fullGamma = register("Full Gamma", true);
    private final FloatSetting chunkDelay = register("Chunk Delay", 0.0f, 0.0f, 300.0f);
    private final BooleanSetting noParticles = register("No Particles", false);
    private final BooleanSetting noEffects = register("No Effects", false);

    @EventListener
    public void onRender3D(final Render3DEvent event) {
        if (customTime.getValue()) {
            mc.world.setTotalWorldTime(time.getValue());
        }
        if (clearWeather.getValue()) {
            mc.world.setRainStrength(0);
        }
        if (fullGamma.getValue() && mc.gameSettings.gammaSetting != 1000) {
            mc.gameSettings.gammaSetting = 1000;
        }
        if (customFov.getValue()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue());
        }
    }

    @EventListener
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (noEffects.getValue() && event.getPacket() instanceof SPacketEffect) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onParticle(final ParticleEvent event) {
        if (noParticles.getValue()) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onChunkLoad(final ChunkLoadEvent event) {
        event.delay = chunkDelay.getValue().longValue();
    }


}
