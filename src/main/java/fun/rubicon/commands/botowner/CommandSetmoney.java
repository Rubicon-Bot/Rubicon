package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.botowner
 */
public class CommandSetmoney extends Command {
    public CommandSetmoney(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        int user_set_money = 0;
        if(args.length == 2){
           try {
               user_set_money = Integer.parseInt(args[1]);
           } catch (NumberFormatException exception) {
               sendErrorMessage("The second value is not a number!");
               sendUsageMessage();
               return;
           }
           RubiconBot.getMySQL().updateUserValue(e.getMessage().getMentionedUsers().get(0),"money",String.valueOf(user_set_money));
           sendEmbededMessage("Money of "+ e.getMessage().getMentionedUsers().get(0).getAsMention() + " has been set to " + user_set_money + "Ruby's.");
       }else{
           sendErrorMessage("Only two arguments are needed!");
           sendUsageMessage();
       }

    }

    @Override
    public String getDescription() {
        return "Set a users money to a given amount.";
    }

    @Override
    public String getUsage() {
        return "setmoney <UserAsMention> <amount of money>";
    }

    @Override
    public int getPermissionLevel() {
        return 3;
    }
}
