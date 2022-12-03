package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.bus.EventListener;
import me.alpha432.oyvey.event.events.*;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.impl.BooleanSetting;
import me.alpha432.oyvey.manager.EventManager;
import me.alpha432.oyvey.shader.impl.BlackShader;
import me.alpha432.oyvey.shader.impl.GradientShader;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;

public class Hud extends Module {
    private final ArrayList<Module> modules = new ArrayList<>();
    private final BooleanSetting watermark = register("Watermark", false);
    private final BooleanSetting welcomer = register("Welcomer", false);
    private final BooleanSetting moduleList = register("Module List", false);
    private final BooleanSetting customHotbar = register("Custom Hotbar", false);
    private final BooleanSetting fps = register("FPS", false);
    //private final BooleanSetting server = register("Server", false);
    //private final BooleanSetting ping = register("Ping", false);
    //private final BooleanSetting potion = register("Potion", false);
    //private final BooleanSetting speed = register("Speed", false);
    //private final BooleanSetting time = register("Time", false);
    //private final BooleanSetting tps = register("TPS", false);

    @EventListener
    public void onRender2D(final Render2DEvent event) {
        if (watermark.getValue()) {
            registerHudText(OyVey.MODNAME + " " + OyVey.MODVER, 0.0f, 0.0f, false);
        }
        
            //if (this.server.getValue()) {
                //String sText = grayString + "Server " + ChatFormatting.WHITE + (Util.mc.isSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(Util.mc.getCurrentServerData()).serverIP);
                //i += 10;
               // this.renderer.drawString(sText, (width - this.renderer.getStringWidth(sText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
              //  counter1[0] = counter1[0] + 1;
            }

            //if (this.speed.getValue()) {
                //String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
                //i += 10;
                //this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
              //  counter1[0] = counter1[0] + 1;
            }
            //if (this.time.getValue()) {
                //String str = grayString + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                //i += 10;
                //this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                //counter1[0] = counter1[0] + 1;
            }
            //if (this.tps.getValue()) {
              //  String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
                //i += 10;
                //this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                //counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            //String sText = grayString + "Server " + ChatFormatting.WHITE + (Util.mc.isSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(Util.mc.getCurrentServerData()).serverIP);
            //String str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    i += 10;
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                //if (this.ping.getValue()) {
                //    i += 10;
              //      this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
            //        counter1[0] = counter1[0] + 1;
                }
            }
        }// else {

            //if (potions.getValue()) {
                //List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                //for (PotionEffect potionEffect : effects) {
                //    String str = OyVey.potionManager.getColoredPotionString(potionEffect);
              //      renderer.drawString(str, (width - renderer.getStringWidth(str) - 2), (2 + i++ * 10), this.potionSync.getValue() ? ((ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            //if (this.server.getValue()) {
                //String sText = grayString + "Server " + ChatFormatting.WHITE + (Util.mc.isSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(Util.mc.getCurrentServerData()).serverIP);
                //this.renderer.drawString(sText, (width - this.renderer.getStringWidth(sText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
              //  counter1[0] = counter1[0] + 1;
            }
            //if (this.speed.getValue()) {
                //String str = grayString + "Speed " + ChatFormatting.WHITE + OyVey.speedManager.getSpeedKpH() + " km/h";
                //this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
              //  counter1[0] = counter1[0] + 1;
            }
            //if (this.time.getValue()) {
                //String str = grayString + " Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
               // this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
              //  counter1[0] = counter1[0] + 1;
            }
            //if (this.tps.getValue()) {
                //String str = grayString + "TPS " + ChatFormatting.WHITE + OyVey.serverManager.getTPS();
                //this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
              //  counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            //String str1 = grayString + "Ping " + ChatFormatting.WHITE + OyVey.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                //if (this.ping.getValue()) {
                  //  this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                    //counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
               // if (this.ping.getValue()) {
                 //   this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color, true);
                   // counter1[0] = counter1[0] + 1;
                }
            }
        }
        
        if (welcomer.getValue()) {
            final String text = "Welcome to " + OyVey.MODNAME + " " + OyVey.MODVER + ", " + mc.player.getName() + "!";
            registerHudText(text, event.scaledResolution.getScaledWidth() / 2.0f - OyVey.textManager.stringWidth(text) / 2.0f, 0.0f, false);
        }
        if (moduleList.getValue()) {
            OyVey.moduleManager.modules.stream().filter(module -> module.isEnabled() && !modules.contains(module)).forEach(modules::add);
            modules.sort(Comparator.comparing(Module::totalStringWidth));
            float deltaY = 0.0f;
            for (final Module module : new ArrayList<>(modules)) {
                if (!module.drawn.getValue()) {
                    continue;
                }
                module.anim = MathUtil.lerp(module.anim, module.isEnabled() ? 1.0f : 0.0f, 0.005f * EventManager.deltaTime);
                final float x = event.scaledResolution.getScaledWidth() + (module.anim * (module.stringWidth() + module.infoWidth()));
                if (!module.isEnabled() && module.anim < 0.05f) {
                    modules.remove(module);
                }
                registerHudText(module.getName(), x, deltaY, false);
                registerHudText(module.getInfo(), x - module.stringWidth(), deltaY, true);
                deltaY += OyVey.textManager.stringHeight() * module.anim;
            }
        }
    }


    private void registerHudText(final String text, final float x, final float y, final boolean gray) {
        if (gray) {
            OyVey.textManager.renderString(text, x, y, Color.GRAY);
            BlackShader.setup();
            OyVey.textManager.renderStringShadowOnly(text, x, y);
            BlackShader.finish();
            return;
        }
        if (!ClickGui.Instance.mode.getValue().equals("Static")) {
            BlackShader.setup();
            OyVey.textManager.renderStringShadowOnly(text, x, y);
            BlackShader.finish();
        } else {
            OyVey.textManager.renderStringShadowOnly(text, x, y);
        }

        GradientShader.setup();
        OyVey.textManager.renderStringNoShadow(text, x, y, ClickGui.Instance.getColor());
        GradientShader.finish();
    }

    @EventListener
    public void onRenderHotbar(final RenderHotbarEvent event){
        if (!customHotbar.getValue()){
            return;
        }
        final ScaledResolution scaledResolution = event.scaledResolution;
        final float centerX = scaledResolution.getScaledWidth() / 2.0f;
        final float height = scaledResolution.getScaledHeight();
        GradientShader.setup();
        float x = -81.0f;
        for (int i = 0; i < 9; i++){
            RenderUtil.texturedOutline(centerX + x, height - 18.0f, centerX + x + 18.0f, height);
            x += 18.0f;
        }
        GradientShader.finish();

        x = -81.0f;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.currentItem == i){
                RenderUtil.rect(centerX + x + 1, height - 17.0f, centerX + x + 17.0f, height - 1, new Color(0, 0, 0, 150));
            }
            final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            glPushMatrix();
            glClear(256);
            RenderHelper.enableStandardItemLighting();
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_BLEND);
            mc.getRenderItem().zLevel = -150.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) (centerX + x + 1.0f), (int) (height - 17.0f));
            mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, (int) (centerX + x + 1.0f), (int) (height - 17.0f));
            mc.getRenderItem().zLevel = 0.0f;
            RenderHelper.disableStandardItemLighting();
            glDisable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            glPopMatrix();
            x += 18.0f;
        }
        event.setCancelled(true);
    }
    @EventListener
    public void onRenderPotionEffects(final RenderPotionEffectsEvent event) {
        event.setCancelled(true);
    }
}
