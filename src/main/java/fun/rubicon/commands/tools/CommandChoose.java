package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandChoose extends CommandHandler {
    public CommandChoose() {
        super(new String[]{"choose"}, CommandCategory.TOOLS, new PermissionRequirements("choose", false, true), "Choose an option", "<option1> <option2> [option3] ...");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        String[] args = invocation.getArgs();
        if (args.length < 2)
            return createHelpMessage();
        int option = ThreadLocalRandom.current().nextInt(args.length);
        return message(EmbedUtil.success(invocation.translate("command.choose.chosen.title"), String.format(invocation.translate("command.choose.chosen.description"), args[option])));
    }
}
