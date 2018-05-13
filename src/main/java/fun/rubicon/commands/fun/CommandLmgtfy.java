package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CommandLmgtfy extends CommandHandler {

    public CommandLmgtfy() {
        super(new String[]{"lmgtfy"}, CommandCategory.FUN, new PermissionRequirements("lmgtfy", false, true), "Generates a lmgtfy link.", "<searcg>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws UnsupportedEncodingException {
        if (invocation.getArgs().length < 1)
            return createHelpMessage(invocation);

        return new MessageBuilder("http://lmgtfy.com/?iie=1&q=" + URLEncoder.encode(invocation.getArgsString(), "UTF-8")).build();
    }
}

