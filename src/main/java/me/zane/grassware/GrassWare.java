package me.zane.grassware;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.discord.Discord;
import me.zane.grassware.event.bus.EventBus;
import me.zane.grassware.features.gui.alt.AltGui;
import me.zane.grassware.manager.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

@Mod(modid = "grassware.win", name = "GrassWare.win", version = GrassWare.MODVER)
public class GrassWare {
    public static final String MODNAME = "GrassWare.win";
    public static final String MODVER = "v1.0";
    public static Minecraft mc;
    public static EventBus eventBus;
    public static ThreadManager threadManager;
    public static HoleManager holeManager;
    public static CommandManager commandManager;
    public static  FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static MotionPredictionManager motionPredictManager;
    public static InventoryManager inventoryManager;
    public static ColorManager colorManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static ServerManager serverManager;

    public static void load() {
        mc = Minecraft.getMinecraft();
        threadManager = new ThreadManager();
        holeManager = new HoleManager();
        eventBus = new EventBus();
        textManager = new TextManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        inventoryManager = new InventoryManager();
        moduleManager = new ModuleManager();
        motionPredictManager = new MotionPredictionManager();
        eventManager = new EventManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        configManager = new ConfigManager();
        serverManager = new ServerManager();
        moduleManager.init();
        configManager.init();
        AltGui.loadAlts();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.saveConfig(GrassWare.configManager.config.replaceFirst("grassware/", ""));
            AltGui.saveAlts();
        }));
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle("GrassWare " + GrassWare.MODVER);
        GrassWare.load();
        Discord.startRPC();

    }

}