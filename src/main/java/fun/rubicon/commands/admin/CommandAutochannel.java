package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

public class CommandAutochannel extends Command {

    public CommandAutochannel(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if(args.length != 2) {
            switch (args[0]) {
                case "add":
                    break;
                case "remove":
                    break;
                default:
                    sendUsageMessage();
                    break;
            }
        } else
            sendUsageMessage();
    }

    private void addChannel(VoiceChannel ch) {

    }

    private void removeChannel(VoiceChannel ch) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "autochannel add [channelname] | Existing Channel\n" +
                "autochannel remove [channelname] | Remove Channel from autochannels";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
