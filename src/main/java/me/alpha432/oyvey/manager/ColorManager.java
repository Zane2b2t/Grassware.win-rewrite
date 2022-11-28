package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.features.modules.client.ClickGui;

import java.awt.*;

public class ColorManager {

    public Color getColorWithAlpha(int alpha) {
        return new Color(ClickGui.Instance.red.getValue() / 255.0f, ClickGui.Instance.green.getValue() / 255.0f, ClickGui.Instance.blue.getValue() / 255.0f, alpha / 255.0f);
    }
}