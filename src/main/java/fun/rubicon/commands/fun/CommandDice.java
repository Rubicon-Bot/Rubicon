package fun.rubicon.commands.fun;

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

public class CommandDice extends CommandHandler {
    public CommandDice() {
        super(new String[] {"dice"}, CommandCategory.FUN, new PermissionRequirements("dice", false, true), "Roll a dice", "[max]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws Exception {
        int max = 6;
        int min = 1;
        String[] args = invocation.getArgs();
        if(args.length >= 1){
            try {
                max = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.dice.invalidnumber.title"), invocation.translate("command.dice.invalidnumber.description")));
            }
        }

        if(max<=1){
            return message(error(invocation.translate("command.dice.invalidmin.title"),invocation.translate("command.dice.invalidmin.description")));
        }

        int random = ThreadLocalRandom.current().nextInt(min, max);
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.dice.rolled.title"), String.format(invocation.translate("command.dice.rolled.description"), random)));
    }
}
