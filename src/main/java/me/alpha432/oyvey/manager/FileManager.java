package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager extends Feature {

    public FileManager() {
        Path base = getMkDirectory(getRoot(), "grassware");
        getMkDirectory(base, "pvp");
        for (Module.Category category : OyVey.moduleManager.getCategories()) {
            Path config = getMkDirectory(base, "config");
            getMkDirectory(config, category.getName());
        }
    }
    
    private Path lookupPath(Path root, String... paths) {
        return Paths.get(root.toString(), paths);
    }

    private Path getRoot() {
        return Paths.get("");
    }

    private void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir)) {
                if (Files.exists(dir)) {
                    Files.delete(dir);
                }
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getMkDirectory(Path parent, String... paths) {
        if (paths.length < 1) {
            return parent;
        }
        Path dir = lookupPath(parent, paths);
        createDirectory(dir);
        return dir;
    }
}

