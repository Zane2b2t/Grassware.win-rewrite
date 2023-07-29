package me.zane.grassware.features.gui.alt;

import me.zane.grassware.GrassWare;
import me.zane.grassware.util.MC;
import me.zane.grassware.util.RenderUtil;
import me.zane.grassware.util.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class AltGui implements MC {
    private static final Timer typingIconTimer = new Timer();
    public static ArrayList<AltButton> altButtons = new ArrayList<>();
    private static String email = "", password = "";
    private static boolean e = false, p = false, microsoft = false;

    public static void drawScreen(final int mouseX, final int mouseY) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final float width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight(), x = width - 150.0f;
        final float center = x + 75.0f;

        // Currently logged in as
        final String text = "Currently logged in as " + mc.session.getUsername() + ".";
        GrassWare.textManager.renderString(text, width / 2.0f - GrassWare.textManager.stringWidth(text) / 2.0f, height / 2.0f + 20.0f, Color.WHITE);

        // Background
        RenderUtil.rect(x, 0, width, height, new Color(14, 14, 14));

        // Top Text
        RenderUtil.rect(x, 0, width, 20, new Color(12, 12, 12));
        RenderUtil.rect(x, 19, width, 20, new Color(20, 20, 20));
        GrassWare.textManager.renderString("Alt Manager", center - GrassWare.textManager.stringWidth("Alt Manager") / 2.0f, 10 - GrassWare.textManager.stringHeight() / 2.0f, Color.WHITE);


        // Buttons background
        RenderUtil.rect(x, height - 85.0f, width, height, new Color(12, 12, 12));
        RenderUtil.rect(x, height - 85.0f, width, height - 84.0f, new Color(20, 20, 20));

        boolean canAdd = !password.equals("") && !email.equals("");

        // Add
        RenderUtil.rect(x + 5, height - 30, x + 72.5f, height - 10, new Color(20, 20, 20));
        GrassWare.textManager.renderString("Add Alt", x + 36.25f - GrassWare.textManager.stringWidth("Add Alt") / 2.0f, height - 20 - GrassWare.textManager.stringHeight() / 2.0f, canAdd ? Color.WHITE : Color.GRAY);
        if (mouseX > x + 5.0f && mouseX < x + 72.5f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
            RenderUtil.rect(x + 5, height - 30, x + 72.5f, height - 10, new Color(0, 0, 0, 50));
        }

        // Microsoft/Cracked
        RenderUtil.rect(x + 77.5f, height - 30, width - 5.0f, height - 10, microsoft ? Color.CYAN : Color.RED);
        if (mouseX > x + 77.5f && mouseX < width - 5.0f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
            RenderUtil.rect(x + 77.5f, height - 30, width - 5.0f, height - 10, new Color(0, 0, 0, 150));
        }
        GrassWare.textManager.renderString(microsoft ? "Microsoft" : "Cracked", x + 112.5f - GrassWare.textManager.stringWidth(microsoft ? "Microsoft" : "Cracked") / 2.0f, height - 20 - GrassWare.textManager.stringHeight() / 2.0f, Color.WHITE);

        // Password
        RenderUtil.rect(x + 5, height - 55, width - 5, height - 35, new Color(20, 20, 20));
        if (password.equals("")) {
            GrassWare.textManager.renderString("Password" + (p ? typingIcon() : ""), x + 7.5f, height - 45 - GrassWare.textManager.stringHeight() / 2.0f, new Color(100, 100, 100, 50));
        } else {
            GrassWare.textManager.renderString(password + (p ? typingIcon() : ""), x + 7.5f, height - 45 - GrassWare.textManager.stringHeight() / 2.0f, Color.WHITE);
        }

        // Email
        RenderUtil.rect(x + 5, height - 80, width - 5, height - 60, new Color(20, 20, 20));
        if (email.equals("")) {
            GrassWare.textManager.renderString((microsoft ? "Email" : "Username") + (e ? typingIcon() : ""), x + 7.5f, height - 70 - GrassWare.textManager.stringHeight() / 2.0f, new Color(100, 100, 100, 50));
        } else {
            GrassWare.textManager.renderString(email + (e ? typingIcon() : ""), x + 7.5f, height - 70 - GrassWare.textManager.stringHeight() / 2.0f, Color.WHITE);
        }

        // Render each alt button
        altButtons.forEach(altButton -> {
            altButton.x = x + 5.0f;
            altButton.drawScreen(mouseX, mouseY);
        });
    }

    public static void keyTyped(final char typedChar, final int keyCode) {
        if (e) {
            email = type(typedChar, keyCode, email);
        } else if (p) {
            password = type(typedChar, keyCode, password);
        }
    }

    public static void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final float width = scaledResolution.getScaledWidth(), height = scaledResolution.getScaledHeight(), x = width - 150.0f;
        if (mouseButton == 0) {
            if (!password.equals("") && !email.equals("") && mouseX > x + 5.0f && mouseX < x + 72.5f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
                altButtons.add(new AltButton(email, password, microsoft ? Alt.AltType.MICROSOFT : Alt.AltType.CRACKED, x + 5.0f, 25 + (altButtons.size() * 50.0f), 140.0f, 45.0f));
                email = "";
                password = "";
            }
            if (mouseX > x + 77.5f && mouseX < width - 5.0f && mouseY > height - 30.0f && mouseY < height - 10.0f) {
                microsoft = !microsoft;
                email = "";
                password = "";
            }
            if (mouseX > x + 5.0f && mouseX < width - 5.0f) {
                e = mouseY > height - 80.0f && mouseY < height - 60.0f;
                p = mouseY > height - 55.0f && mouseY < height - 35.0f;
            }
        }
        new ArrayList<>(altButtons).forEach(altButton -> altButton.mouseClicked(mouseX, mouseY, mouseButton)); //e
    }

    public static void updateButtons() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final float width = scaledResolution.getScaledWidth(), x = width - 150.0f;
        final ArrayList<AltButton> altButtons1 = new ArrayList<>();
        for (AltButton altButton : altButtons) {
            altButtons1.add(new AltButton(altButton.email, altButton.password, altButton.altType, x + 5.0f, 25 + (altButtons1.size() * 50.0f), 140.0f, 45.0f));
        }
        altButtons = altButtons1;
    }

    private static String typingIcon() {
        if (typingIconTimer.passedMs(1000)) {
            typingIconTimer.sync();
        }
        if (typingIconTimer.passedMs(500)) {
            return "";
        }
        return "_";
    }

    private static String type(final char typedChar, final int keyCode, final String string) {
        String newString = string;
        switch (keyCode) {
            case 14:
                if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                    newString = "";
                }
                if (newString.length() > 0) {
                    newString = newString.substring(0, newString.length() - 1);
                }
                break;
            case 27:
            case 28:
                e = false;
                p = false;
                break;
            default:
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    newString = newString + typedChar;
                    break;
                }
        }
        return newString;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveAlts() {
        File file = new File("grassware/alts.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            for (AltButton altButton : altButtons) {
                writer.write(altButton.email + ":" + altButton.password + ":" + altButton.altType.equals(Alt.AltType.MICROSOFT) + "\n");
            }
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAlts() {
        File file = new File("grassware/alts.txt");
        try {
            if (file.exists()) {
                final ScaledResolution scaledResolution = new ScaledResolution(mc);
                final float width = scaledResolution.getScaledWidth(), x = width - 150.0f;
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                bufferedReader.lines().forEach(line -> {
                    final String[] split = line.split(":");
                    altButtons.add(new AltButton(split[0], split[1], Boolean.parseBoolean(split[2]) ? Alt.AltType.MICROSOFT : Alt.AltType.CRACKED, x + 5.0f, 25 + (altButtons.size() * 50.0f), 140.0f, 45.0f));
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
