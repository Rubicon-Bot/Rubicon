package fun.rubicon.core.music;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class GuildMusicPlayerManager {

    private HashMap<Long, GuildMusicPlayer> playerStorage = new HashMap<>();

    /**
     * Updates the cached GuildMusicPlayer
     *
     * @param invocation
     * @param permissions
     * @throws IllegalStateException when player is not cached
     */
    public void updatePlayer(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        if (!playerStorage.containsKey(invocation.getGuild().getIdLong()))
            throw new IllegalStateException("The provided Guild has no cached GuildMusicPlayer");
        GuildMusicPlayer player = playerStorage.get(invocation.getGuild().getIdLong());
        player.invocation = invocation;
        player.userPermissions = permissions;
        playerStorage.replace(invocation.getGuild().getIdLong(), player);
    }

    private void updatePlayer(GuildMusicPlayer player) {
        playerStorage.replace(player.invocation.getGuild().getIdLong(), player);
    }

    public GuildMusicPlayer getAndCreatePlayer(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        GuildMusicPlayer player;
        if (!playerStorage.containsKey(invocation.getGuild().getIdLong()))
            player = new GuildMusicPlayer(invocation, permissions);
        else {
            player = playerStorage.get(invocation.getGuild().getIdLong());
            player.invocation = invocation;
            player.userPermissions = permissions;
            updatePlayer(player);
        }
        playerStorage.put(invocation.getGuild().getIdLong(), player);
        return player;
    }

    public GuildMusicPlayer getPlayerByGuild(Guild guild) {
        return playerStorage.get(guild.getIdLong());
    }

    public HashMap<Long, GuildMusicPlayer> getPlayerStorage() {
        return playerStorage;
    }

}
