package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.features.setting.impl.IntSetting;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render2DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;

public class CSGOWatermark extends Module {

    Timer delayTimer = new Timer();

    public float hue;
    public int red = 1;
    public int green = 1;
    public int blue = 1;

    @Override
    public void onRender2D(Render2DEvent event) {
        drawCsgoWatermark();
    }

    public void drawCsgoWatermark() {
        int padding = 5;
        String message = "Grassware.win v" + OyVey.MODVER + " | " + mc.player.getName() + " | " + OyVey.serverManager.getPing() + "ms";
        int textWidth = OyVey.textManager.getStringWidth(message); // taken from wurst+ 3
        int textHeight = mc.fontRenderer.FONT_HEIGHT; // taken from wurst+ 3
        RenderUtil.drawRectangleCorrectly(X.getValue() - 4, Y.getValue() - 4, textWidth + 16, textHeight + 12);
        RenderUtil.drawRectangleCorrectly(X.getValue(), Y.getValue(), textWidth + 4, textHeight + 4); 
        RenderUtil.drawRectangleCorrectly(X.getValue(), Y.getValue(), textWidth + 8, textHeight + 4);
        RenderUtil.drawRectangleCorrectly(X.getValue(), Y.getValue(), textWidth + 8, 1); 
        OyVey.textManager.drawString(message, X.getValue() + 3, Y.getValue() + 3);
    }
}
