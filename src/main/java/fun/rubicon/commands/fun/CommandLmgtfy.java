package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

/**
 * Amme JDA BOT
 * <p>
 * By LordLee at 18.11.2017 17:47
 * <p>
 * Contributors for this class:
 * - github.com/zekrotja
 * - github.com/DRSchlaubi
 * <p>
 * © Coders Place 2017
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
            for(int i = 0; i < args.length; i++){
                query += " " + args[i];
            }
            String url = "http://lmgtfy.com/?iie=1&q=" + query.replace( " ", "%20");

            sendEmbededMessage("Link created send the following link to the person which needs help " + url);
        }}


    @Override
    public String getDescription() {
        return "Creates a Lmgtfy link for a person which who not wants to google himself";
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
