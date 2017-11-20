package fun.rubicon.commands.fun;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Bitly;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

public class CommandShort extends Command{
    /**
     * Rubicon Discord bot
     *
     * @author Schlaubi
     * @copyright Rubicon Dev Team 2017
     * @license MIT License <http://rubicon.fun/license>
     * @package fun.rubicon.util
     */

    public CommandShort(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        if(args.length > 0) {
            Bitly bitly = new Bitly(Info.BITLY_TOKEN);
            try{
                sendEmbededMessage("Shorted URL: " + bitly.generateShortLink(args[0]));
            } catch (Exception ex){
                sendErrorMessage(":warning: Please enter a valid URL");
            }
        } else {
            sendUsageMessage();
        }

    }

    @Override
    public String getDescription() {
        return "Shorts a URL with schlb.pw";
    }

    @Override
    public String getUsage() {
        return "short <longurl>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
