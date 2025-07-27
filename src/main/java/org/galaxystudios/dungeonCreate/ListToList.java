package org.galaxystudios.dungeonCreate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListToList {
    public static List<String> toList(String input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(input.split(",")).map(String::trim).collect(Collectors.toList());
    }

}
