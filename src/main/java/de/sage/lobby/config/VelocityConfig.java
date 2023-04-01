package de.sage.lobby.config;

import de.sage.lobby.LobbySystem;
import lombok.SneakyThrows;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;

import java.io.*;
import java.nio.file.Path;

public class VelocityConfig {
    private YAMLConfigurationLoader configLoader;
    private final Path configDirectory;
    private boolean firstRun = true;

    /**
     * Object for managing the config
     *
     * @param directory Data directory of the plugin
     * @param fileName  Name of the config file
     */
    public VelocityConfig(@NotNull Path directory, @NotNull String fileName) {
        this.configDirectory = directory;

        try {
            if (directory.toFile().exists()) {
                if (directory.resolve(fileName).toFile().createNewFile()) {
                    generateConfig(fileName);
                } else
                    firstRun = false;

            } else {
                if (directory.toFile().mkdirs())
                    directory.resolve(fileName).toFile().createNewFile();

                generateConfig(fileName);
            }

            this.configLoader = YAMLConfigurationLoader.builder().setPath(directory.resolve(fileName)).setFlowStyle(DumperOptions.FlowStyle.BLOCK).build();

          /*  if (!firstRun) {
                updateConfig(configLoader, YAMLConfigurationLoader.builder().setSource(() -> new BufferedReader(new InputStreamReader(LobbySystem.class.getResourceAsStream("/config.yml")))).setFlowStyle(DumperOptions.FlowStyle.BLOCK).build());
            }*/
        } catch (IOException exception) {
            exception.printStackTrace();
            LobbySystem.getInstance().getLogger().error("Error while creating config file! Please report this to the developer!");
        }
    }

    /**
     * Return the value of the given path or the default value
     *
     * @param defaultValue Default if no value was found on the path
     * @param path         Path to find the value
     * @return The value to the path or default
     */
    @SneakyThrows(IOException.class)
    public <T> T getOption(@Nullable T defaultValue, @NotNull String path) {
        return configLoader.load().getNode(path).getValue() != null ? (T) configLoader.load().getNode(path).getValue() : defaultValue;
    }

    /**
     * Sets the value to the given path
     *
     * @param value Value to set
     * @param path  Path of the value to set
     * @throws IOException On any IO error
     * @implNote This is not working because I don't save the config again. I won't do that cause of the same reason why config updating doesn't work rn
     */
    public void setValue(@NotNull Object value, @NotNull String path) throws IOException {
        ConfigurationNode node = configLoader.load();
        node.getNode(path).setValue(value);
        configLoader.save(node);
    }

    /**
     * Generates the contents of the config file
     *
     * @param filename The filename to put the values in
     * @throws IOException On any IO error
     */
    private void generateConfig(@NotNull String filename) throws IOException {
        LobbySystem.getInstance().getLogger().info("Generating config...");

        InputStream configStream = LobbySystem.class.getResourceAsStream("/config.yml");
        File config = configDirectory.resolve(filename).toFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(config));
        BufferedReader reader = new BufferedReader(new InputStreamReader(configStream));

        int charactar;
        while (!((charactar = reader.read()) == -1)) {
            writer.write(charactar);
        }

        writer.close();
        reader.close();

        LobbySystem.getInstance().getLogger().info("Config generated!");
    }

   /* @SneakyThrows(IOException.class)
    private void updateConfig(YAMLConfigurationLoader oldConfig, YAMLConfigurationLoader newConfig) {
        LobbySystem.getInstance().getLogger().info("Updating config file to new version...");
        ConfigurationNode updatedConfig = oldConfig.load();

        newConfig.load().getChildrenMap().forEach((key, value) -> {
            if (updatedConfig.getNode(key).getValue() == null) {
                updatedConfig.getNode(key).setValue(value);
            }
        });

        oldConfig.save(updatedConfig);
        oldConfig.load();
        LobbySystem.getInstance().getLogger().info("Configfile update finished!");
    }*/
}