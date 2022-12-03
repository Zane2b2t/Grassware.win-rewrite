package me.alpha432.oyvey.manager;

import com.google.gson.*;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.features.setting.impl.*;
import me.alpha432.oyvey.util.MC;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigManager implements MC {
    public final  ArrayList<Feature> features = new ArrayList<>();
    public String config = "grassware/config/";

    public static void setValueFromJson(final Setting<?> setting, final JsonElement element) {
        if (setting instanceof BooleanSetting) {
            ((BooleanSetting) setting).invokeValue(element.getAsBoolean());
            return;
        }
        if (setting instanceof FloatSetting) {
            ((FloatSetting) setting).invokeValue(element.getAsFloat());
            return;
        }
        if (setting instanceof IntSetting) {
            ((IntSetting) setting).invokeValue(element.getAsInt());
            return;
        }
        if (setting instanceof StringSetting) {
            ((StringSetting) setting).invokeValue(element.getAsString());
            return;
        }
        if (setting instanceof ModeSetting) {
            ((ModeSetting) setting).invokeValue(element.getAsString());
            return;
        }
        if (setting instanceof BindSetting) {
            ((BindSetting) setting).invokeValue(element.getAsInt());
        }
    }

    private static void loadFile(final JsonObject input, final Feature feature) {
        for (Map.Entry<String, JsonElement> entry : input.entrySet()) {
            String settingName = entry.getKey();
            JsonElement element = entry.getValue();
            if (feature instanceof FriendManager) {
                try {
                    OyVey.friendManager.addFriend(settingName);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                continue;
            }
            for (Setting<?> setting : feature.getSettings()) {
                if (settingName.equals(setting.getName())) {
                    try {
                        setValueFromJson(setting, element);
                        if (setting instanceof BooleanSetting && setting.getName().equals("Enabled") && ((BooleanSetting) setting).getValue()) {
                            if (feature instanceof ClickGui){
                                ((BooleanSetting) setting).invokeValue(false);
                            } else {
                                OyVey.eventBus.registerListener(feature);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadConfig(final String name) {
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("grassware").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        if (files.contains(new File("grassware/" + name + "/"))) {
            this.config = "grassware/" + name + "/";
        } else {
            this.config = "grassware/config/";
        }
        OyVey.friendManager.onLoad();
        for (Feature feature : this.features) {
            try {
                loadSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveCurrentConfig();
    }

    public boolean configExists(final String name) {
        final List<File> files = Arrays.stream(Objects.requireNonNull(new File("grassware").listFiles())).filter(File::isDirectory).collect(Collectors.toList());
        return files.contains(new File("grassware/" + name + "/"));
    }

    public void saveConfig(final String name) {
        this.config = "grassware/" + name + "/";
        File path = new File(this.config);
        if (!path.exists())
            path.mkdir();
        OyVey.friendManager.saveFriends();
        for (Feature feature : this.features) {
            try {
                saveSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveCurrentConfig();
    }

    public void saveCurrentConfig() {
        File currentConfig = new File("grassware/currentconfig.txt");
        try {
            if (currentConfig.exists()) {
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("grassware", ""));
                writer.close();
            } else {
                currentConfig.createNewFile();
                FileWriter writer = new FileWriter(currentConfig);
                String tempConfig = this.config.replaceAll("/", "");
                writer.write(tempConfig.replaceAll("grassware", ""));
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadCurrentConfig() {
        File currentConfig = new File("grassware/currentconfig.txt");
        String name = "config";
        try {
            if (currentConfig.exists()) {
                Scanner reader = new Scanner(currentConfig);
                while (reader.hasNextLine())
                    name = reader.nextLine();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public void saveSettings(final Feature feature) throws IOException {
        File directory = new File(this.config + getDirectory(feature));
        if (!directory.exists())
            directory.mkdir();
        String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
        Path outputFile = Paths.get(featureName);
        if (!Files.exists(outputFile))
            Files.createFile(outputFile);
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        String json = gson.toJson(writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    public void init() {
        this.features.addAll(OyVey.moduleManager.modules);
        this.features.add(OyVey.friendManager);
        String name = loadCurrentConfig();
        loadConfig(name);
    }

    private void loadSettings(final Feature feature) throws IOException {
        String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName);
        if (!Files.exists(featurePath))
            return;
        loadPath(featurePath, feature);
    }

    private void loadPath(final Path path, final Feature feature) throws IOException {
        InputStream stream = Files.newInputStream(path);
        try {
            loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException e) {
            loadFile(new JsonObject(), feature);
        }
        stream.close();
    }

    public JsonObject writeSettings(final Feature feature) {
        final JsonObject object = new JsonObject();
        final JsonParser jp = new JsonParser();
        for (Setting<?> setting : feature.getSettings()) {
            String value = setting.getValue().toString();
            if (setting instanceof StringSetting) {
                value = ((StringSetting) setting).getValue().replace(" ", "_");
            }
            try {
                object.add(setting.getName(), jp.parse(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public String getDirectory(final Feature feature) {
        String directory = "";
        if (feature instanceof Module)
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        return directory;
    }
}
