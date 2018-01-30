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
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandVolume extends CommandHandler {

    public CommandVolume() {
        super(new String[]{"volume"}, CommandCategory.MUSIC, new PermissionRequirements(PermissionLevel.EVERYONE, "command.volume"), "Control the volume of the bot.", "<amount>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        UserSQL userSQL = new UserSQL(parsedCommandInvocation.getAuthor());

        if (!userSQL.isPremium()) {
            return EmbedUtil.message(EmbedUtil.noPremium());
        }
        MusicManager manager = new MusicManager(parsedCommandInvocation);

        Message msg = manager.executeVolume();
        if (msg == null)
            return createHelpMessage();
        else
            return msg;
    }
}
