package fun.rubicon.command;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.command
 */
@Getter
@AllArgsConstructor
public enum CommandCategory {

    TEST("test", "Test"),
    GENERAL("general", "General"),
    FUN("fun", "Fun"),
    MODERATION("mod", "Moderation"),
    ADMIN("admin", "Admin"),
    GUILD_OWNER("guildOwner", "Server Owner"),
    BOT_OWNER("botOwner", "Bot Owner"),
    TOOLS("tools", "Tools"),
    SETTINGS("settings", "Settings"),
    MUSIC("music", "Music");

    private final String id;
    private final String displayname;

}