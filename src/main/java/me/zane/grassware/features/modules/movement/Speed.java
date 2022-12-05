package me.zane.grassware.features.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.KeyEvent;
import me.zane.grassware.event.events.MoveEvent;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.BindSetting;
import me.zane.grassware.features.setting.impl.ModeSetting;
import me.zane.grassware.util.EntityUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;

import java.util.Arrays;
import java.util.Objects;

public class Speed extends Module {
    private final ModeSetting mode = register("Mode", "Strafe", Arrays.asList("Strafe", "OnGround"));
    private final BindSetting switchBind = register("Switch Bind", -1);
    private double previousDistance, motionSpeed;
    private int currentState = 1;

    @EventListener
    public void onKey(final KeyEvent event) {
        if (event.key == switchBind.getValue()) {
            mode.invokeValue(mode.getValue().equals("Strafe") ? "OnGround" : "Strafe");
            Command.sendRemovableMessage( ChatFormatting.WHITE + "Speed mode switched to " + ChatFormatting.BOLD + mode.getValue(), 1);
        }
    }

    @EventListener
    public void onMove(final MoveEvent event) {
        if (mode.getValue().equals("Strafe")) {
            switch (currentState) {
                case 0:
                    ++currentState;
                    previousDistance = 0.0;
                    break;
                case 1:
                default:
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && currentState > 0) {
                        currentState = mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f ? 0 : 1;
                    }
                    motionSpeed = previousDistance - previousDistance / 159.0f;
                    break;
                case 2:
                    double y = 0.40123128;
                    if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            y += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
                        }
                        event.setMotionY(mc.player.motionY = y);
                        motionSpeed *= 2.149f;
                    }
                    break;
                case 3:
                    motionSpeed = previousDistance - 0.76f * (previousDistance - EntityUtil.getBaseMotionSpeed() * 1.05f);
            }
            motionSpeed = Math.max(motionSpeed, EntityUtil.getBaseMotionSpeed() * 1.05f);
            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            double yaw = mc.player.rotationYaw;
            if (forward != 0.0 && strafe != 0.0) {
                forward *= Math.sin(0.7853981633974483);
                strafe *= Math.cos(0.7853981633974483);
            }
            if (event.motionY < 0.0f) {
                event.setMotionY(event.motionY - 0.15f);
            }
            event.setMotionX((forward * motionSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * motionSpeed * Math.cos(Math.toRadians(yaw))) * 0.99f);
            event.setMotionZ((forward * motionSpeed * Math.cos(Math.toRadians(yaw)) - strafe * motionSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99f);
            ++currentState;
        } else {
            if (!(mc.player.isSneaking() || mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) || !mc.player.onGround) {
                MovementInput movementInput = mc.player.movementInput;
                float moveForward = movementInput.moveForward;
                float moveStrafe = movementInput.moveStrafe;
                float rotationYaw = mc.player.rotationYaw;
                if ((moveForward == 0.0 && moveStrafe == 0.0)) {
                    event.setMotionX(0.0);
                    event.setMotionZ(0.0);
                } else {
                    if (moveForward != 0.0) {
                        if (moveStrafe > 0.0) {
                            rotationYaw += (moveForward > 0.0 ? -45 : 45);
                        } else if (moveStrafe < 0.0) {
                            rotationYaw += moveForward > 0.0 ? 45 : -45;
                        }
                        moveStrafe = 0.0f;
                    }
                    moveStrafe = moveStrafe == 0.0f ? moveStrafe : (moveStrafe > 0.0 ? 1.0f : -1.0f);
                    final double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
                    final double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
                    event.setMotionX(moveForward * EntityUtil.getMaxSpeed() * cos + moveStrafe * EntityUtil.getMaxSpeed() * sin);
                    event.setMotionZ(moveForward * EntityUtil.getMaxSpeed() * sin - moveStrafe * EntityUtil.getMaxSpeed() * cos);
                }
            }
        }
    }
}
