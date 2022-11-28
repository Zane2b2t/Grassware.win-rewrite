package me.alpha432.oyvey.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class OyVeyLoader
        implements IFMLLoadingPlugin {

    public OyVeyLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.oyvey.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(final Map<String, Object> map) {}

    public String getAccessTransformerClass() {
        return null;
    }
}

