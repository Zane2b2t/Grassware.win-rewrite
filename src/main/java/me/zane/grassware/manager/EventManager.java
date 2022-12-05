package me.zane.grassware.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zane.grassware.GrassWare;
import me.zane.grassware.event.events.*;
import me.zane.grassware.features.Feature;
import me.zane.grassware.features.command.Command;
import me.zane.grassware.event.events.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;


public class EventManager extends Feature {
    public static long lastFrame, deltaTime;

    public EventManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(final TickEvent event) {
        if (!nullCheck()) {
            final me.zane.grassware.event.events.TickEvent tickEvent = new me.zane.grassware.event.events.TickEvent();
            GrassWare.eventBus.invoke(tickEvent);
        }
    }

    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent event) {
        if (!nullCheck()) {
            mc.profiler.startSection("oyvey");

            final Render3DPreEvent render3dPreEvent = new Render3DPreEvent(event.getPartialTicks());
            GrassWare.eventBus.invoke(render3dPreEvent);

            final Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
            GrassWare.eventBus.invoke(render3dEvent);

            deltaTime = System.currentTimeMillis() - lastFrame;
            lastFrame = System.currentTimeMillis();

            mc.profiler.endSection();
        }
    }


    @SubscribeEvent
    public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Text event) {
        if (!nullCheck()) {
            final Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), event.getResolution());
            GrassWare.eventBus.invoke(render2DEvent);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Post event) {
        final Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), event.getResolution());
        GrassWare.eventBus.invoke(render2DEvent);
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (!nullCheck()) {
            final OverlayEvent overlayEvent = new OverlayEvent(event.getOverlayType());
            GrassWare.eventBus.invoke(overlayEvent);
            if (overlayEvent.isCancelled()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        final ItemInputUpdateEvent itemInputUpdateEvent = new ItemInputUpdateEvent(event.getMovementInput());
        GrassWare.eventBus.invoke(itemInputUpdateEvent);
    }

    @SubscribeEvent
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            GrassWare.moduleManager.onKeyPressed(Keyboard.getEventKey());
        }
    }

    @SubscribeEvent
    public void onChatSent(final ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);
            try {
                mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                if (event.getMessage().length() > 1) {
                    GrassWare.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Please enter a command.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Command.sendMessage(ChatFormatting.RED + "An error occurred while running this command. Check the log!");
            }
        }
    }
}
