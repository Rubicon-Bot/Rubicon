package fun.rubicon.commands.botowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandBroadcast extends Command {
    public CommandBroadcast(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if(args.length<1)
        {
            sendUsageMessage();
            return;
        }
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage("broadcast") {
        return null;
    }

    @Override
    public int getPermissionLevel() {
        return 4;
    }
}
