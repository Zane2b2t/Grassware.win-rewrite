package me.zane.grassware.featurws.modules.misc;

import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import me.zane.grassware.modules.Module;
import me.zane.grassware.features.setting.impl.Switch;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class FakePlayer extends Module {
    public static FakePlayer Instance;
    public final Switch copyInventory = Menu.Switch("Copy Inventory");
    EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        Instance = this;
    }

    public static UUID getUUIDByName(String name) {
        try {
            URLConnection request = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
            request.connect();
            String id = java.util.UUID.fromString(new JsonParser().parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonObject().get("id").getAsString().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")).toString();
            return UUID.fromString(id);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public void onEnable() {
        if (nullCheck())
            return;
        fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(getUUIDByName("FakePlayer"), "FakePlayer"));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        if (copyInventory.GetSwitch()) {
            fakePlayer.inventory = mc.player.inventory;
        }
        fakePlayer.setHealth(36);
        mc.world.addEntityToWorld(-100, fakePlayer);
    }

    @Override
    public void onTick() {
        if (fakePlayer != null && fakePlayer.getDistanceSq(mc.player) > (100 * 100)) {
            mc.world.removeEntityFromWorld(-100);
            disableModule("FakePlayer too far away.");
        }
    }

    @Override
    public void onDisable() {
        if (fakePlayer != null)
            mc.world.removeEntityFromWorld(-100);
    }
}
