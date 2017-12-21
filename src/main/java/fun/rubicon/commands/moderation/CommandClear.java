package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.tools
 */
public class CommandClear extends CommandHandler{
    private int getInt(String string){
        try {
            return Integer.parseInt(string);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    public CommandClear() {
        super(new String[]{"clear","purge"},CommandCategory.MODERATION,new PermissionRequirements(PermissionLevel.WITH_PERMISSION,"command.clear"),"Clear the chat.","<amount of messages>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        //Delete Message and Get amount of Messages(If no number -> error)
        parsedCommandInvocation.invocationMessage.delete().queue();
        if (parsedCommandInvocation.args.length < 1)
            return new MessageBuilder().setEmbed(EmbedUtil.error("", "Please give an amount of Messages!").build()).build();
        int numb = getInt(parsedCommandInvocation.args[0]);
        //Check if amount is Ok for Discord API
        if (numb >= 2 && numb <= 100) {
            try {
                //Try to get Messages of Channel
                MessageHistory history = new MessageHistory(parsedCommandInvocation.invocationMessage.getChannel());
                List<Message> messages;
                messages = history.retrievePast(numb).complete();
                parsedCommandInvocation.invocationMessage.getTextChannel().deleteMessages(messages).queue();
                int number = numb - 1;
                //User Feedback
                Message msg = parsedCommandInvocation.invocationMessage.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(Colors.COLOR_PRIMARY)
                        .setDescription(":bomb: Deleted " + number + " Messages!")
                        .build()
                ).complete();
                //Delete User Feedback
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        msg.delete().queue();
                    }
                }, 3000);
            } catch (Exception fuck) {
                fuck.printStackTrace();
            }
        } else {
            return new MessageBuilder().setEmbed(EmbedUtil.error("", getUsage()).build()).build();
        }

        return null;
    }}
