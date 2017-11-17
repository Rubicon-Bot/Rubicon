package de.rubicon.core;

import de.rubicon.util.Configuration;
import de.rubicon.util.Info;
import java.io.File;

public class Main {

    public static Configuration getConfiguration() {
        return configuration;
    }

    private static Configuration configuration;

    public static void main(String[] args) {
        configuration = new Configuration(new File(Info.CONFIG_FILE));
        DiscordCore.start();
    }
}
