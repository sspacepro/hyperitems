package org.galaxystudios.hyperitems.LoadPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadElementBeatsMap {

    private static final Map<String, List<String>> elementBeats = new HashMap<>();

    // Static load method to populate the map
    public static void load() {
        elementBeats.clear();
        elementBeats.put("Earth", Arrays.asList("Electric", "Toxic"));
        elementBeats.put("Water", Arrays.asList("Lava", "Toxic"));
        elementBeats.put("Lava", Arrays.asList("Ice", "Crystal"));
        elementBeats.put("Air", Arrays.asList("Earth", "Steam"));
        elementBeats.put("Space", Arrays.asList("Time", "Illusion"));
        elementBeats.put("Light", Arrays.asList("Darkness", "Illusion"));
        elementBeats.put("Darkness", Arrays.asList("Mind", "Air"));
        elementBeats.put("Electric", Arrays.asList("Water", "Ice"));
        elementBeats.put("Plasma", Arrays.asList("Metal", "Stone"));
        elementBeats.put("Ice", Arrays.asList("Water", "Plasma"));
        elementBeats.put("Metal", Arrays.asList("Air", "Earth"));
        elementBeats.put("Crystal", Arrays.asList("Light", "Mind"));
        elementBeats.put("Steam", Arrays.asList("Toxic", "Ice"));
        elementBeats.put("Spirit", Arrays.asList("Darkness", "Plasma"));
        elementBeats.put("Time", Arrays.asList("Decay", "Stone"));
        elementBeats.put("Void", Arrays.asList("Light", "Spirit"));
        elementBeats.put("Toxic", Arrays.asList("Spirit", "Crystal"));
        elementBeats.put("Illusion", Arrays.asList("Mind", "Void"));
        elementBeats.put("Mind", Arrays.asList("Time", "Space"));
        elementBeats.put("Decay", Arrays.asList("Spirit", "Metal"));
        elementBeats.put("Stone", Arrays.asList("Crystal", "Steam"));
    }

    // Getter method to access the map
    public static Map<String, List<String>> getElementBeatsMap() {
        return elementBeats;
    }


}
