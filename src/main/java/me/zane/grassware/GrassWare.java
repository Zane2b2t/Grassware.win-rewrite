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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configManager.saveConfig(GrassWare.configManager.config.replaceFirst("grassware/", ""));
            AltGui.saveAlts();
        }));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GrassWare.load();

        // Create a Timer and a TimerTask to randomly change the Display title every 5 seconds
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            String[] options = {"Brought to you by ZANE#4417", "Rat#7632 is a NOOB", "GrassWare ON TOP!", "MrBubbleCu- Gum is cool"};

            Random random = new Random();

            @Override
            public void run() {
                int index = random.nextInt(options.length);
                Display.setTitle(options[index]);
            }
        };
        timer.schedule(task, 0, 5000); // Run the task every 5 seconds

        Discord.startRPC();
    }
}