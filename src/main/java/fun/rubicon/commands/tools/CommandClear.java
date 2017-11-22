package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

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
public class CommandClear extends Command{
    private int getInt(String string){
        try {
            return Integer.parseInt(string);
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    public CommandClear(String command, CommandCategory category) {
        super(command, category);
    }
    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        e.getMessage().delete().queue();
        if (args.length < 1) sendErrorMessage("Please give an amount of Messages!");
        int numb = getInt(args[0]);
        if(numb>= 2 && numb<=100){
            try{
                MessageHistory history = new MessageHistory(e.getChannel());
                List<Message> messages;
                messages = history.retrievePast(numb).complete();
                e.getTextChannel().deleteMessages(messages).queue();
                int number = numb-1;
                Message msg = e.getChannel().sendMessage(new EmbedBuilder()
                        .setColor(Colors.COLOR_PRIMARY)
                        .setDescription(":bomb: Deleted " + number + " Messages!")
                        .build()
                ).complete();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        msg.delete().queue();
                    }
                }, 3000);
            }catch (Exception fuck){
                fuck.printStackTrace();
            }
        }else {
            sendUsageMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Clears the given amount of messages.";
    }

    @Override
    public String getUsage() {
        return "clear <amountofmessages>";
    }

    @Override
    public int getPermissionLevel() {
        return 1;
    }
}
