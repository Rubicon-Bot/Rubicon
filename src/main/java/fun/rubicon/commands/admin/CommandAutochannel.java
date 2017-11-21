package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

public class CommandAutochannel extends Command {

    public CommandAutochannel(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "autochannel create [channelname] | New Channel\n" +
                "autochannel add [channelname] | Existing Channel\n" +
                "autochannel remove [channelname] | Remove Channel from autochannels\n" +
                "autochannel delete [channelname] | Deltes Channel";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
