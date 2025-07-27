package org.galaxystudios.dungeonCreate;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LoadEntityElements {
    private final static LoadEntityElements instance = new LoadEntityElements();

    private YamlConfiguration config;

    private LoadEntityElements() {


    }

    public void load() throws IOException, InvalidConfigurationException {
        InputStream inputStream = LoadEntityElements.class.getResourceAsStream("/EntityElements.yml"))

        Yaml yaml = new Yaml;
        Map<String, Object> data = yaml.load(inputStream);

    }

    public static LoadEntityElements getInstance() {
        return instance;
    }
}
