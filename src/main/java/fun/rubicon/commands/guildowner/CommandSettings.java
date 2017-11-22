package fun.rubicon.commands.guildowner;

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
        if(args.length < 2) {
            sendUsageMessage();
            return;
        }
        Guild guild = e.getGuild();
        MessageChannel channel = e.getTextChannel();
        if (!Main.getMySQL().ifGuildExits(e.getGuild())){
            Main.getMySQL().createGuildServer(e.getGuild());
        }
        String joinmessage = Main.getMySQL().getGuildValue(guild, "joinmsg");
        switch (args[0].toLowerCase()) {
            case "prefix":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage());
                    return;
                }
                Main.getMySQL().updateGuildValue(guild, "prefix", args[1]);
                sendEmbededMessage(":white_check_mark: Successfully set the prefix!");
                break;
            case "logchannel":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage());
                    return;
                }
                String txt = e.getMessage().getMentionedChannels().get(0).getId();
                Main.getMySQL().updateGuildValue(guild, "logchannel", txt);
                sendEmbededMessage(":white_check_mark: Successfully set the LogChannel!");
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
                if (e.getMessage().getMentionedRoles().size()<1){
                    try {
                        Role role = e.getGuild().getRolesByName(args[1], true).get(0);
                        Main.getMySQL().updateGuildValue(guild, "autorole", role.getId());
                    }catch (Exception er){
                        sendErrorMessage("Please enter a Valid Role!");
                        return;
                    }
                }else if (e.getMessage().getMentionedRoles().size()>1){
                    try {
                        Role role = e.getMessage().getMentionedRoles().get(0);
                        Main.getMySQL().updateGuildValue(guild, "autorole", role.getId());
                    }catch (ArrayIndexOutOfBoundsException er){
                        sendErrorMessage("Please enter a Valid Role!");
                        return;
                    }

                }

                sendEmbededMessage(":white_check_mark: Succesfully set the Autorole!");
                break;
            case "channel":
                if (args.length < 2) {
                    e.getChannel().sendMessage(getUsage() + "\n(watch for large and lower case\n)");
                    return;
                }
                String ch = e.getMessage().getMentionedChannels().get(0).getId();
                Main.getMySQL().updateGuildValue(guild, "channel", ch);
                sendEmbededMessage(":white_check_mark: Successfully set the Joinmessagechannel!");
                break;
            case "welcomemsg":
                if (args.length<2){
                    e.getTextChannel().sendMessage(getUsage());
                    return;
                }
                if (Main.getMySQL().getGuildValue(guild, "welmsg").equals("0")){
                    Main.getMySQL().updateGuildValue(guild, "welmsg", "1");
                }else {
                    Main.getMySQL().updateGuildValue(guild, "welmsg", "0");
                }
                sendEmbededMessage(":white_check_mark: Successfully toggled the PNWelcomeMessage!");
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
                "settings logchannel <Mention channel> (Set the logchannel | 0 for no Channel.)\n\n" +
                "settings prefix <NEWPREFIX> (Set the new Bot Prefix for this Guild)\n\n" +
                "settings autorole <ROLENAME/ROLEMENTION> (Set the Autorole at UserJoin | 0 for no Role.)\n\n" +
                "settings joinmessage <Message> (%user% for the Username %guild% for Guildname) (0 for no message)\n\n" +
                "settings channel <Channel> (Set the Channel for Join Messages |Mention the channel for the Joinmessage)\n\n"+
                "settings welcomemsg (Toggles the Private Welcome Message)";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
