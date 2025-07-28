package org.galaxystudios.dungeonCreate;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//This loads all entity custom element data from EntityElements.yml to mobElementMap.

public class LoadEntityElements {
    @Getter
    private final static LoadEntityElements instance = new LoadEntityElements();


    private LoadEntityElements() {


    }

    public void load() {
        InputStream inputStream = LoadEntityElements.class.getResourceAsStream("/EntityElements.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Object elementObj = data.get("Element");

        Map<String, String> mobElementMap = new HashMap<>();

        if (elementObj instanceof Map<?, ?> rawMap) {
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();

                if (key instanceof String elementType && value != null) {

                    String[] entities = value.toString().split("\\s*,\\s*");

                    for (String entity : entities) {
                        mobElementMap.put(entity, elementType);
                    }
                }
            }
        }

    }

}
