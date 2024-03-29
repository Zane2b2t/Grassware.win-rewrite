package me.zane.grassware.manager;
//WARNING: ALL CONTENT BELONGS TO https://github.com/Zane2b2t , IF ANY OF THE CLASSES CONTAINING THIS WARNING ARENT IN https://github.com/Zane2b2t/Grassware.win-Rewrite INFORM GITHUB TO DMCA
import me.zane.grassware.features.Feature;
import me.zane.grassware.features.modules.Module;
import me.zane.grassware.util.ClassFinder;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class ModuleManager extends Feature {
    private static final LinkedHashMap<String, Module> modulesNameMap = new LinkedHashMap<>();
    public ArrayList<Module> modules = new ArrayList<>();

    public static Module getModule(String name) {
        if (name == null) return null;
        return modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }

    public void init() {
        try {
            for (Module.Category category : Module.Category.values()) {
                final ArrayList<Class<?>> classes = ClassFinder.classesExtending(ClassFinder.classesInFolder(category.getName().toLowerCase()), Module.class);
                for (Class<?> c : classes) {
                    final String moduleName = c.getName().split("\\.")[6];
                    final Module module = ((Module) c.getConstructor().newInstance()).setName(moduleName).setCategory(category);
                    modules.add(module);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen != null) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind() == eventKey) {
                module.toggle();
            }
        });
    }
}

