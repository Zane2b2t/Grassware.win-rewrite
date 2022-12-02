package me.alpha432.oyvey.module.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;


public class QuickBow extends Module {
    public QuickBow() {
        super("QuickBow", Category.COMBAT, "Releases bow at a very high speed");
    }

    @Override
    public void onUpdate() {
        if (nullCheck())
            return;

        if (InventoryUtil.getHeldItem(Items.BOW) && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
    }
}
