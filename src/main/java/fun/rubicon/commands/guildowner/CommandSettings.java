package fun.rubicon.commands.guildowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.Main;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.guildowner
 */
public class CommandSettings extends Command {
    public CommandSettings(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        if (args.length < 2) {
            sendUsageMessage();
            return;
        }
        Guild guild = e.getGuild();
        MessageChannel channel = e.getTextChannel();
        if (!Main.getMySQL().ifGuildExits(e.getGuild())) {
            Main.getMySQL().createGuildServer(e.getGuild());
        }
        String joinmessage = Main.getMySQL().getGuildValue(guild, "joinmsg");
        switch (args[0].toLowerCase()) {
            case "logchannel":
                String txt = e.getMessage().getMentionedChannels().get(0).getId();
                RubiconBot.getMySQL().updateGuildValue(guild, "logchannel", txt);
                sendEmbededMessage(":white_check_mark: Successfully set the LogChannel!");
                break;
            case "joinmessage":
                String temp = "";
                for (int i = 1; i < args.length; i++) {
                    temp += " " + args[i];
                }
                RubiconBot.getMySQL().updateGuildValue(guild, "joinmsg", temp.replaceFirst("null ", ""));
                String up = RubiconBot.getMySQL().getGuildValue(guild, "joinmsg");
                sendEmbededMessage(":white_check_mark:  Successfully set joinmessage to `" + up + "`!");
                break;
            case "autorole":
                if(args[1].equals("0")) {
                    RubiconBot.getMySQL().updateGuildValue(guild, "autorole", "0");
                    sendEmbededMessage(":white_check_mark:  Successfully set autorole to `0`!");
                    return;
                }
                if (e.getMessage().getMentionedRoles().size() < 1) {
                    try {
                        Role role = e.getGuild().getRolesByName(args[1], true).get(0);
                        RubiconBot.getMySQL().updateGuildValue(guild, "autorole", role.getId());
                    } catch (Exception er) {
                        sendErrorMessage("Please enter a Valid Role!");
                        return;
                    }
                } else if (e.getMessage().getMentionedRoles().size() > 1) {
                    try {
                        Role role = e.getMessage().getMentionedRoles().get(0);
                        RubiconBot.getMySQL().updateGuildValue(guild, "autorole", role.getId());
                    } catch (ArrayIndexOutOfBoundsException er) {
                        sendErrorMessage("Please enter a Valid Role!");
                        return;
                    }

                }

                sendEmbededMessage(":white_check_mark: Succesfully set the Autorole!");
                break;
            case "channel":
                String ch = e.getMessage().getMentionedChannels().get(0).getId();
                RubiconBot.getMySQL().updateGuildValue(guild, "channel", ch);
                sendEmbededMessage(":white_check_mark: Successfully set the Joinmessagechannel!");
                break;
            default:
                sendUsageMessage();
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
                        "settings autorole <rolename/@role> (Sets the autorole | 0 for no Role.)\n" +
                        "settings joinmessage <Message> (%user% for the Username %guild% for Guildname) (0 for no message)\n" +
                        "settings channel <Channel> (Set the Channel for Join Messages |Mention the channel for the Joinmessage)";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
