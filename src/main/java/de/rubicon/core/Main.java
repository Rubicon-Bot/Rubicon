package de.rubicon.core;

public class Main {

    private static DiscordCore discordCore;

    public static void main(String[] args) {
        discordCore = new DiscordCore();
        discordCore.start();
    }

    public static DiscordCore getDiscordCore() {
        return discordCore;
    }
}
