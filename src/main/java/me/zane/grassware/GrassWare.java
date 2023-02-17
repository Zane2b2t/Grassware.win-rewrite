package me.zane.grassware;

import me.zane.grassware.discord.Discord;
import me.zane.grassware.event.bus.EventBus;
import me.zane.grassware.features.gui.alt.AltGui;
import me.zane.grassware.manager.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.opengl.Display;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = "grassware.win", name = "GrassWare.win", version = GrassWare.MODVER)
public class GrassWare {
    public static final String MODNAME = "GrassWare.win";
    public static final String MODVER = "v0.6";
    public static Minecraft mc;
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
    public static RotationManager rotationManager;
    private static Timer titleUpdateTimer;

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
        eventManager = new EventManager();
        fileManager = new FileManager();
        colorManager = new ColorManager();
        configManager = new ConfigManager();
        rotationManager = new RotationManager();
        moduleManager.init();
        configManager.init();
        AltGui.loadAlts();

        // Schedule a task to update the title every 5 seconds
        titleUpdateTimer = new Timer();
        titleUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Display.setTitle(getRandomTitle());
            }
        }, 0, 5000);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.saveConfig(GrassWare.configManager.config.replaceFirst("grassware/", ""));
            AltGui.saveAlts();
        }));
    }

    private static String getRandomTitle() {
        // Define a list of custom messages
        String[] messages = {"GrassWare " + MODVER, "Best Minecraft Hacked Client!", "Get GrassWare today!", "Don't play Minecraft without GrassWare!", "GrassWare - Your new best friend!"};

        // Choose a random message from the list
        Random random = new Random();
        int index = random.nextInt(messages.length);
        return messages[index];
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Set the initial title
        Display.setTitle(getRandomTitle());

        // Load the rest of the client
        GrassWare.load();
        Discord.startRPC();
    }
}
