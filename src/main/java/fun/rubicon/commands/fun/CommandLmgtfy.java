package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.fun
 */
public class CommandLmgtfy extends Command{
    public CommandLmgtfy(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Message message = e.getMessage();
        MessageChannel channel = e.getTextChannel();
        message.delete().queue();

        if(args.length > 0){
            String query = "";
            for (String arg : args) {
                query += " " + arg;
            }
            String url = "http://lmgtfy.com/?iie=1&q=" + query.replace( " ", "%20");

            sendEmbededMessage("Link created send the following link to the person which needs help " + url);
        }}


    @Override
    public String getDescription() {
        return "Creates a Lmgtfy link for a person which who not wants to google himself.";
    }

    @Override
    public String getUsage() {
        return "lmgtfy <Querry>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
