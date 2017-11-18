package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandUserInfo extends Command{
    public CommandUserInfo(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {

    }

    @Override
    public String getDescription() {
        return "Returns some information about the specified user";
    }

    @Override
    public String getUsage() {
        return "::userinfo [@User]";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
