package me.zane.grassware.features.modules.player;

import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BooleanSetting;

public class TpsSync
        extends Module {
    private static TpsSync INSTANCE = new TpsSync();
    public final BooleanSetting attack = register("Attack", false);
    public final BooleanSetting mining = register("Mine", false);

    public TpsSync() {
        this.setInstance();
    }

    public static TpsSync getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TpsSync();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
