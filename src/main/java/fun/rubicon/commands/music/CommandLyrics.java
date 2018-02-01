package fun.rubicon.commands.music;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.music.MusicManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandLyrics extends CommandHandler{
    public CommandLyrics() {
        super(new String[] {"lyrics", "songtext"}, CommandCategory.MUSIC, new PermissionRequirements(PermissionLevel.EVERYONE, "command.lyrics"), "Displays the current song's lyrics", "lyrics", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        UserSQL user = UserSQL.fromUser(parsedCommandInvocation.getAuthor());

        if(!user.isPremium())
            return new MessageBuilder().setEmbed(EmbedUtil.noPremium().build()).build();

        MusicManager musicManager = new MusicManager(parsedCommandInvocation);

        return musicManager.executeLyrics();
    }
}
