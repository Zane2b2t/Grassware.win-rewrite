package me.zane.grassware.features.modules.render;
// this no work i commented shit for it to build
import me.zane.grassware.event.bus.EventListener;
import me.zane.grassware.event.events.RenderToolTipEvent;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.impl.FloatSetting;
import me.zane.grassware.features.setting.impl.IntSetting;
//import me.zane.grassware.shader.impl.BlurShader;
import me.zane.grassware.shader.impl.ShadowGradientShader;
import me.zane.grassware.util.RenderUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class ShulkerViewer extends Module {
    private final IntSetting radius = register("Radius", 5, 2, 10);
    private final FloatSetting intensity = register("Intensity", 1.0f, 1.0f, 2.0f);

    @EventListener
    public void onRenderToolTip(RenderToolTipEvent event) {
        if (event.getItemStack().getItem() instanceof ItemShulkerBox) {
            NBTTagCompound tagCompound = event.getItemStack().getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
                NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
                if (!blockEntityTag.hasKey("Items", 9)) {
                    return;
                }
                event.setX(event.getX() + 10);
                event.setY(event.getY() + 10);
                float width = 162.5f, height = 57.5f;

                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, items);

                GlStateManager.disableDepth();
              //  RenderUtil.rounded(event.getX(), event.getY(), event.getX() + width, event.getY() + height, 5.0f, new Color(48, 51, 71, 150));

              //  BlurShader.invokeBlur();
              //  RenderUtil.rounded(event.getX(), event.getY(), event.getX() + width, event.getY() + height, 5.0f, Color.WHITE);
              //  BlurShader.releaseBlur(10.0f);

                ShadowGradientShader.invokeShadow();
              //  RenderUtil.rounded(event.getX(), event.getY(), event.getX() + width, event.getY() + height, 5.0f, Color.WHITE);
               // ShadowGradientShader.releaseShadow();

                int item = 0;
                float deltaX = event.getX() + 2.5f, deltaY = event.getY() + 2.5f;
                for (ItemStack itemStack : items) {
                    if (item == 9) {
                        deltaX = event.getX() + 2.5f;
                        deltaY += 17.5f;
                        item = 0;
                    }
                    String s = itemStack.isEmpty() ?TextFormatting.YELLOW + "0" : null;
                    mc.getRenderItem().zLevel = 500.0f;
                    GlStateManager.enableDepth();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) deltaX, (int) deltaY);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, (int) deltaX, (int) deltaY, s);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableDepth();
                    mc.getRenderItem().zLevel = 0.0f;
                    deltaX += 17.5f;
                    item++;
                }
                GlStateManager.enableDepth();

                event.setCancelled(true);
            }
        }
    }

}
