package me.alpha432.oyvey.features.modules.player;

import com.mojang.authlib.GameProfile;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import java.util.UUID;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class FakePlayer extends Module {
    public FakePlayer() {
        super("FakePlayer", "Spawns a literal fake player", Module.Category.PLAYER, false, false, false);
    }

    public Setting<String> fakename = register(new Setting("Name", "nigga"));

    private EntityOtherPlayerMP clonedPlayer;

    public void onEnable() {
        Command.sendMessage(fakename.getValueAsString() + " has been spawned");
        if (mc.player == null || mc.player.isDead) {
            disable();
            return;
        }

        clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("0f75a81d-70e5-43c5-b892-f33c524284f2"), fakename.getValueAsString()));
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-12345, clonedPlayer);
        clonedPlayer.onLivingUpdate();
    }

    public void onDisable() {
        if (mc.world != null) {
            mc.world.removeEntityFromWorld(-12345);
        }
    }

    @Override
    public void onLogout() {
        if (this.isOn()) {
            this.disable();
        }
    }
}
