package fun.rubicon.commands.botowner;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.commands.admin.CommandVerification;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandAlarm extends CommandHandler {

    public CommandAlarm() {
        super(new String[]{"god"}, CommandCategory.BOT_OWNER, new PermissionRequirements("command.alarm", true, false), "Nice Stuff :scream:", "codingguy");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if(parsedCommandInvocation.getArgs().length == 0) {
            return EmbedUtil.message(EmbedUtil.error("Dein Ernst?", ":facepalm:"));
        }
        switch (parsedCommandInvocation.getArgs()[0]) {
            case "codingguy":
                CommandVerification.toggleInspired();
                return EmbedUtil.message(EmbedUtil.success("Toggled", "Successfully toggled the `inspired`."));
        }
        return createHelpMessage();
    }
}
