package me.alpha432.oyvey;

import me.alpha432.oyvey.event.bus.EventBus;
import me.alpha432.oyvey.features.gui.alt.AltGui;
import me.alpha432.oyvey.manager.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

@Mod(modid = "oyvey", name = "OyVey", version = "0.0.3")
public class OyVey {
    public static final String MODNAME = "OyVey";
    public static final String MODVER = "0.0.3";
    public static EventBus eventBus;
    public static ThreadManager threadManager;
    public static HoleManager holeManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ColorManager colorManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static EventManager eventManager;
    public static TextManager textManager;

    public static void load() {
        threadManager = new ThreadManager();
        holeManager = new HoleManager();
        eventBus = new EventBus();
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        configManager = new ConfigManager();
        moduleManager.init();
        configManager.init();
        AltGui.loadAlts();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.saveConfig(OyVey.configManager.config.replaceFirst("oyvey/", ""));
            AltGui.saveAlts();
        }));
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("OyVey v0.0.3");
        OyVey.load();
    }
}

