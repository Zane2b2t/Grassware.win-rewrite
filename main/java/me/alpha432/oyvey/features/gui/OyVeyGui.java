package me.alpha432.oyvey.features.gui;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.gui.components.Component;
import me.alpha432.oyvey.features.gui.components.items.buttons.ModuleButton;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class OyVeyGui extends GuiScreen {
    private static OyVeyGui INSTANCE = new OyVeyGui();
    private final ArrayList<Component> components = new ArrayList<>();

    public OyVeyGui() {
        setInstance();
        load();
    }

    public static OyVeyGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OyVeyGui();
        }
        return INSTANCE;
    }

    public static OyVeyGui getClickGui() {
        return OyVeyGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (final Module.Category category : OyVey.moduleManager.getCategories()) {
            components.add(new Component(category.getName(), x += 90, 4, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    OyVey.moduleManager.getModulesByCategory(category).forEach(module -> addButton(new ModuleButton(module)));
                }
            });
        }
        components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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

