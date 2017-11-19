package fun.rubicon.commands.guildowner;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 17.11.2017 20:29
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
 */
public class CommandSettings extends Command {
    public CommandSettings(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {

        Guild guild = e.getGuild();
        MessageChannel channel = e.getTextChannel();
        if (!Main.getMySQL().ifGuildExits(e.getGuild())){
            Main.getMySQL().createGuildServer(e.getGuild());
        }
        //Permission here
        if (args.length < 2) e.getChannel().sendMessage(getUsage());
        String joinmessage = Main.getMySQL().getGuildValue(guild, "joinmsg");
        switch (args[0].toLowerCase()) {
            case "prefix":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage());
                    return;
                }
                Main.getMySQL().updateGuildValue(guild, "prefix", args[1]);
                sendEmbededMessage(":white_check_mark: Succesfully set the Prefix!");
                break;
            case "logchannel":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage());
                    return;
                }
                String txt = e.getMessage().getMentionedChannels().get(0).getId();
                Main.getMySQL().updateGuildValue(guild, "logchannel", txt);
                sendEmbededMessage(":white_check_mark: Succesfully set the LogChannel!");
                break;
            case "joinmessage":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage() + "\n(watch for large and lower case\n)");
                    return;
                }
                String temp = "";
                for(int i = 1; i < args.length; i++){
                    temp += " " + args[i];
                }
                Main.getMySQL().updateGuildValue(guild, "joinmsg", temp.replaceFirst("null ", ""));
                String up = Main.getMySQL().getGuildValue(guild, "joinmsg");
                sendEmbededMessage(":white_check_mark:  Successfully set joinmessage to `" + up + "`!");
                break;
            case "autorole":
                if (args.length < 2) {
                    e.getTextChannel().sendMessage(getUsage() + "\n(watch for large and lower case\n)");
                    return;
                }
                Main.getMySQL().updateGuildValue(guild, "autorole", args[1]);
                sendEmbededMessage(":white_check_mark: Succesfully set the Autorole!");
                break;
            case "channel":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage() + "\n(watch for large and lower case\n)");
                    return;
                }
                String ch = e.getMessage().getMentionedChannels().get(0).getId();
                Main.getMySQL().updateGuildValue(guild, "channel", ch);
                sendEmbededMessage(":white_check_mark: Succesfully set the Joinmessagechannel!");
                break;
        }
    }

    @Override
    public String getDescription() {
        return "Set up guild specific settings!";
    }

    @Override
    public String getUsage() {
        return
                "settings logchannel <Mention channel> (Set the logchannel | 0 for no Channel.)\n" +
                "settings prefix <NEWPREFIX> (Set the new Bot Prefix for this Guild)\n" +
                "settings autorole <ROLENAME> (Set the Autorole at UserJoin | 0 for no Role.)\n" +
                "settings joinmessage <Message> (%user% for the Username %guild% for Guildname) (0 for no message)\n" +
                "settings channel <Channel> (Mention the channel for the Joinmessage)";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
