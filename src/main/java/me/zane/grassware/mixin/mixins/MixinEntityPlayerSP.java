package me.zane.grassware.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.EventStage;
import me.zane.grassware.event.events.*;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Unique
    private float savedYaw, savedPitch;
    public MixinEntityPlayerSP(final World worldIn, final GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"))
    public void sendChatMessage(final String message, final CallbackInfo ci) {
        final ChatEvent chatEvent = new ChatEvent(message);
        GrassWare.eventBus.invoke(chatEvent);
    }

    public void move(final MoverType type, final double x, final double y, final double z) {
        final MoveEvent event = new MoveEvent(type, x, y, z);
        GrassWare.eventBus.invoke(event);
        super.move(type, event.motionX, event.motionY, event.motionZ);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(final MoverType type, final double x, final double y, final double z, final CallbackInfo ci) {
        final MoveEvent event = new MoveEvent(type, x, y, z);
        GrassWare.eventBus.invoke(event);
        if (event.motionX != x || event.motionY != y || event.motionZ != z) {
            super.move(type, event.motionX, event.motionY, event.motionZ);
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayer(final CallbackInfo ci) {
        final EntityPlayerSP player = EntityPlayerSP.class.cast(this);
        final UpdatePlayerWalkingEvent updatePlayerWalkingEvent = new UpdatePlayerWalkingEvent();
        GrassWare.eventBus.invoke(updatePlayerWalkingEvent);
        if (updatePlayerWalkingEvent.isCancelled()) {
            ci.cancel();
        }
        final MotionUpdateEvent motionUpdateEvent = new MotionUpdateEvent(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        GrassWare.eventBus.invoke(motionUpdateEvent);
        if (motionUpdateEvent.isCancelled()) {
            ci.cancel();
            return;
        }
        savedYaw = player.rotationYaw;
        savedPitch = player.rotationPitch;
        player.rotationYaw = motionUpdateEvent.getYaw();
        player.rotationPitch = motionUpdateEvent.getPitch();
        }
    @Inject(method = "onUpdateWalkingPlayer", at = @At("TAIL"))
    private void onUpdateWalkingPlayerTail(CallbackInfo callback) {
        final EntityPlayerSP player = EntityPlayerSP.class.cast(this);

        GrassWare.eventBus.invoke(new MotionUpdateEvent(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch));

        player.rotationYaw = savedYaw;
        player.rotationPitch = savedPitch;
    }
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    protected void pushOutOfBlocksHook(final double x, final double y, final double z, final CallbackInfoReturnable<Boolean> info) {
        final PushBlockEvent pushBlockEvent = new PushBlockEvent();
        GrassWare.eventBus.invoke(pushBlockEvent);
        if (pushBlockEvent.isCancelled()) {
            info.setReturnValue(false);
        }
    }
}

