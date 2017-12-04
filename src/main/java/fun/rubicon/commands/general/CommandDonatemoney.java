package fun.rubicon.commands.general;

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
 * @package fun.rubicon.commands.general
 */
public class CommandDonatemoney extends Command {
    public CommandDonatemoney(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        int user1_has_money = 0;
        int user2_has_money = 0;
        int user_spend_money = 0;
        if (args.length == 2) {
            try {
<<<<<<< HEAD
                user_spend_money = Integer.parseInt(args[args.length-1]);
                user1_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(e.getAuthor(),"money"));
                user2_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(e.getMessage().getMentionedUsers().get(0),"money"));
                if(user1_has_money < user_spend_money){
=======
                user_spend_money = Integer.parseInt(args[1]);
                user1_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(e.getAuthor(), "money"));
                user2_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(e.getMessage().getMentionedUsers().get(0), "money"));
                if (user1_has_money < user_spend_money) {
>>>>>>> master
                    sendErrorMessage("Sorry " + e.getAuthor().getAsMention() + ". You only have " + user1_has_money + "Ruby's you can donate!");
                } else {
                    RubiconBot.getMySQL().updateUserValue(e.getAuthor(), "money", String.valueOf(user1_has_money - user_spend_money));
                    RubiconBot.getMySQL().updateUserValue(e.getMessage().getMentionedUsers().get(0), "money", String.valueOf(user2_has_money + user_spend_money));
                    sendEmbededMessage(e.getAuthor().getAsMention() + " give " + user_spend_money + " Ruby's to " + e.getMessage().getMentionedUsers().get(0).getAsMention() + ".");
                }
            } catch (NumberFormatException exception) {
                sendErrorMessage("The second value is not a number!");
                sendUsageMessage();
                return;
            }
        } else {
            sendErrorMessage("Only two arguments are needed!");
            sendUsageMessage();
        }
    }

    @Override
    public String getDescription() {
        return "You can give someone some Ruby's!";
    }

    @Override
    public String getUsage() {
        return "givemoney <UserAsMention> <amount>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
