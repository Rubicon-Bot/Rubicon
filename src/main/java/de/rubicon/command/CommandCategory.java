package de.rubicon.command;

public enum CommandCategory {

    TEST("test", "Test"),
    GENERAL("general", "General"),
    FUN("fun", "Fun"),
    TOOLS("tools", "Tools"),
    MODERATION("mod", "Moderation"),
    ADMIN("admin", "Admin"),
    GUILD_OWNER("guildOwner", "Server Owner"),
    BOT_OWNER("botOwner", "Bot Owner"),

    SUB_NONE("subNone", "");

    private String id;
    private String displayname;

    CommandCategory(String id, String displayname) {
        this.id = id;
        this.displayname = displayname;
    }

    public String getId() {
        return id;
    }

    public String getDisplayname() {
        return displayname;
    }
}