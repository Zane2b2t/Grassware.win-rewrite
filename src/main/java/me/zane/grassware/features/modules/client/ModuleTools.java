package me.zane.grassware.features.modules.client;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;

public class ModuleTools extends Module {

    private static ModuleTools INSTANCE;

    public Setting<Notifier> notifier = register(new Setting("ModuleNotifier", Notifier.SYNCCLIENT));



    public static ModuleTools getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleTools();
        }
        return INSTANCE;
    }


    public enum Notifier {
        TROLLGOD,
        PHOBOS,
        FUTURE,
        DOTGOD,
        SYNCCLIENT
    }



}
