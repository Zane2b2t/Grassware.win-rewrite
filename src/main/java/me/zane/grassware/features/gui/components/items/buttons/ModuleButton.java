package me.zane.grassware.features.gui.components.items.buttons;

import me.zane.grassware.features.gui.components.Component;
import me.zane.grassware.features.gui.components.items.Item;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.setting.Setting;
import me.zane.grassware.features.setting.impl.*;
import me.zane.grassware.features.setting.impl.*;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton
        extends Button {
    private final Module module;
    private List<Item> items = new ArrayList<>();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        initSettings();
    }

    public void initSettings() {
        ArrayList<Item> newItems = new ArrayList<>();
        if (!module.getSettings().isEmpty()) {
            for (Setting<?> setting : module.getSettings()) {
                if (setting instanceof BooleanSetting && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton((BooleanSetting) setting));
                }
                if (setting instanceof BindSetting && !setting.getName().equalsIgnoreCase("Keybind") && !module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add(new BindButton((BindSetting) setting));
                }
                if (setting instanceof StringSetting && !setting.getName().equalsIgnoreCase("displayName")) {
                    newItems.add(new StringButton((StringSetting) setting));
                }
                if (setting instanceof IntSetting) {
                    newItems.add(new IntSlider((IntSetting) setting));
                }
                if (setting instanceof FloatSetting) {
                    newItems.add(new FloatSlider((FloatSetting) setting));
                }
                if (setting instanceof ModeSetting) {
                    newItems.add(new EnumButton((ModeSetting) setting));
                }
            }
        }
        newItems.add(new BindButton(module.bind));
        items = newItems;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!items.isEmpty()) {
            if (subOpen) {
                float height = 1.0f;
                for (Item item : items) {
                    Component.counter1[0] = Component.counter1[0] + 1;
                    if (!item.isHidden()) {
                        item.setLocation(x + 1.0f, y + (height += 15.0f));
                        item.setHeight(15);
                        item.setWidth(width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                    }
                    item.update();
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!items.isEmpty()) {
            if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
                subOpen = !subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            if (subOpen) {
                for (Item item : items) {
                    if (item.isHidden()) continue;
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!items.isEmpty() && subOpen) {
            for (Item item : items) {
                if (item.isHidden()) continue;
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (subOpen) {
            int height = 14;
            for (Item item : items) {
                if (item.isHidden()) continue;
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }

    @Override
    public void toggle() {
        module.toggle();
    }

    @Override
    public boolean getState() {
        return module.isEnabled();
    }
}

