package org.galaxystudios.dungeonCreate;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LoadEntityElements {
    @Getter
    private static final LoadEntityElements instance = new LoadEntityElements();

    private final Map<String, String> mobElementMap = new HashMap<>();

    private LoadEntityElements() {

    }

    public void load() {
        mobElementMap.clear();

        try (InputStream inputStream = LoadEntityElements.class.getResourceAsStream("/EntityElements.yml")) {
            if (inputStream == null) {
                throw new IllegalStateException("Resource not found: /EntityElements.yml");
            }

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            Object elementObj = data.get("Element");

            if (elementObj instanceof Map<?, ?> rawMap) {
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    Object key = entry.getKey();
                    Object value = entry.getValue();

                    if (key instanceof String elementType && value != null) {
                        String[] entities = value.toString().split("\\s*,\\s*");
                        for (String entity : entities) {
                            mobElementMap.put(entity.toUpperCase(), elementType); // Convert to uppercase here
                        }
                    }

                }
            }
        } catch (Exception e) {
            DungeonCreate.getInstance().getLogger().severe("Error loading EntityElements.yml: " + e.getMessage());
        }
    }

}
