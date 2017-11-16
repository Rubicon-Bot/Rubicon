package de.rubicon.core;


import de.rubicon.util.Configuration;
import de.rubicon.util.Info;

import java.io.File;
import java.io.IOException;

public class Main {

    private static DiscordCore discordCore;

    public static void main(String[] args) {
        discordCore = new DiscordCore();
        discordCore.start();
    }

    public static DiscordCore getDiscordCore() {
        return discordCore;
    }

    public static Configuration getConfig(){
        File file = new File(Info.CONFIG_FILE);
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return new Configuration(file);
    }
}
