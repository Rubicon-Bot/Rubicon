package de.rubicon.command;

public enum CommandCategory {

<<<<<<< HEAD
    GENERAL,
    id,
    TEST,
    displayname;

=======
    TEST("test", "Test"),
    GENERAL("general", "General"),
    FUN("fun", "Fun"),
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
>>>>>>> 80afd8508f106d6342277fe619c25c22a895a416
}