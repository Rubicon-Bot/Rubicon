package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandSay extends Command {

    public CommandSay(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if(args.length == 0) {
            sendUsageMessage();
            return;
        }

        String text = "";
        for(int i = 0; i < args.length; i++) {
            text += args[i];
        }
        sendEmbededMessage(e.getTextChannel(), e.getMember().getEffectiveName(), Colors.COLOR_PRIMARY, text);
    }

    @Override
    public String getDescription() {
        return "Say some things with the bot!";
    }

    @Override
    public String getUsage() {
        return "say <message>";
    }

    @Override
    public int getPermissionLevel() {
        return 1;
    }
}
