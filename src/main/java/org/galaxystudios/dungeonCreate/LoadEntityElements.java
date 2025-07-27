package org.galaxystudios.dungeonCreate;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LoadEntityElements {
    private final static LoadEntityElements instance = new LoadEntityElements();
    private File file;
    private YamlConfiguration config;

    private LoadEntityElements() {


    }

    public void load() {
        file = new File()


    }

    public static LoadEntityElements getInstance() {
        return instance;
    }
}
