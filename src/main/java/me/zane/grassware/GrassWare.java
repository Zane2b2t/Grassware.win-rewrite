package me.zane.grassware;

import me.zane.grassware.event.bus.EventBus;
import me.zane.grassware.features.gui.alt.AltGui;
import me.zane.grassware.manager.*;
import me.zane.grassware.manager.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

@Mod(modid = "grassware", name = "GrassWare", version = "1.0")
public class GrassWare {
    public static final String MODNAME = "GrassWare";
    public static final String MODVER = "1.0";
    public static EventBus eventBus;
    public static ThreadManager threadManager;
    public static HoleManager holeManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static InventoryManager inventoryManager;
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
        inventoryManager = new InventoryManager();
        moduleManager = new ModuleManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        configManager = new ConfigManager();
        moduleManager.init();
        configManager.init();
        AltGui.loadAlts();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.saveConfig(GrassWare.configManager.config.replaceFirst("oyvey/", ""));
            AltGui.saveAlts();
        }));
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("Grassware v0.0.4");
        GrassWare.load();
    }
}

