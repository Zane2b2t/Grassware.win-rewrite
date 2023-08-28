package me.zane.grassware.features.gui;

import me.zane.grassware.GrassWare;
import me.zane.grassware.features.Feature;
import me.zane.grassware.features.gui.components.Component;
import me.zane.grassware.features.gui.components.items.buttons.ModuleButton;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.features.modules.client.ClickGui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GrassWareGui extends GuiScreen {
    private static GrassWareGui INSTANCE = new GrassWareGui();
    private final ArrayList<Component> components = new ArrayList<>();

    public GrassWareGui() {
        setInstance();
        load();
    }

    public static GrassWareGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GrassWareGui();
        }
        return INSTANCE;
    }

    public static GrassWareGui getClickGui() {
        return GrassWareGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (final Module.Category category : GrassWare.moduleManager.getCategories()) {
            components.add(new Component(category.getName(), x += 90, 4, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    GrassWare.moduleManager.getModulesByCategory(category).forEach(module -> addButton(new ModuleButton(module)));
                }
            });
        }
        components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.Instance.background.getValue()) {
            this.drawDefaultBackground();
        }
        checkMouseWheel();
        components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        components.forEach(components -> components.mouseReleased(releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }
}

