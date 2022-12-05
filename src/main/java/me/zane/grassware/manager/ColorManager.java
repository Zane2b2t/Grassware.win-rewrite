package me.zane.grassware.manager;

import me.zane.grassware.features.modules.client.ClickGui;

import java.awt.*;

public class ColorManager {

    public Color getColorWithAlpha(int alpha) {
        return new Color(ClickGui.Instance.red.getValue() / 255.0f, ClickGui.Instance.green.getValue() / 255.0f, ClickGui.Instance.blue.getValue() / 255.0f, alpha / 255.0f);
    }
}