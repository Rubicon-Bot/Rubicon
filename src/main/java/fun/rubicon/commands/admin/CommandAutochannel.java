package fun.rubicon.commands.admin;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

public class CommandAutochannel extends Command {

    public CommandAutochannel(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                String entry = Main.getMySQL().getGuildValue(e.getGuild(), "autochannels");
                String out = "";
                for(String s : entry.split(",")) {
                    out += e.getJDA().getVoiceChannelById(s).getName() + "\n";
                }
                sendEmbededMessage(e.getTextChannel(),  "Autochannels", Colors.COLOR_PRIMARY, out);
            } else
                sendUsageMessage();
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "create":
                case "c":
                    createChannel(args[1]);
                    break;
                default:
                    sendUsageMessage();
                    break;
            }
        } else
            sendUsageMessage();
    }

    private void createChannel(String name) {
        Channel channel = e.getGuild().getController().createVoiceChannel(name).complete();
        String oldEntry = Main.getMySQL().getGuildValue(e.getGuild(), "autochannels");
        String newEntry = oldEntry + channel.getId() + ",";
        Main.getMySQL().updateGuildValue(e.getGuild(), "autochannels", newEntry);
        sendEmbededMessage(e.getTextChannel(), "Created Autochannel", Colors.COLOR_PRIMARY, "Successfully created autochannel -> " + channel.getName() + "");
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getUsage() {
        return "autochannel create [channelname]\n" +
                "autochannel list";
    }

    @Override
    public int getPermissionLevel() {
        return 2;
    }
}
