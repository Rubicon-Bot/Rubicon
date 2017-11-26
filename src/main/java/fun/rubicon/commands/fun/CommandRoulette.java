package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.minigames.RouletteNumber;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.ParseException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Rubicon Discord bot
 *
 * @author xEiisKeksx
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.fun
 */
public class CommandRoulette extends Command {
    public CommandRoulette(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        String condition = "";
        String user_has_money = RubiconBot.getMySQL().getUserValue(e.getAuthor(),"money");
        if (args.length >= 2) {
            int user_set_money = 0;
            try {
                user_set_money = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                sendUsageMessage();
                return;
            }
            if (Integer.parseInt(user_has_money) < user_set_money) {
                sendErrorMessage(e.getAuthor().getAsMention() + " You don't have enough Ruby. Your entry wasn't accepted.");
            }else{
                int RandomRouletteNumber = ThreadLocalRandom.current().nextInt(0, 36 + 1);
                switch (args[1]){
                    case "red":
                        if(RouletteNumber.RouletteColor[RandomRouletteNumber] == "red"){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *2) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*2);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "black":
                        if(RouletteNumber.RouletteColor[RandomRouletteNumber] == "black"){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *2) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*2);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "evan":
                        break;
                    case "odd":
                        break;
                    case "1-18":
                        break;
                    case "19-36":
                        break;
                    case "1-12":
                        break;
                    case "13-24":
                        break;
                    case "25-36":
                        break;
                    case "column_up":
                        break;
                    case "column_mid":
                        break;
                    case "column_low":
                        break;
                    case "0":
                        break;
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    case "5":
                        break;
                    case "6":
                        break;
                    case "7":
                        break;
                    case "8":
                        break;
                    case "9":
                        break;
                    case "10":
                        break;
                    case "11":
                        break;
                    case "12":
                        break;
                    case "13":
                        break;
                    case "14":
                        break;
                    case "15":
                        break;
                    case "16":
                        break;
                    case "17":
                        break;
                    case "18":
                        break;
                    case "19":
                        break;
                    case "20":
                        break;
                    case "21":
                        break;
                    case "22":
                        break;
                    case "23":
                        break;
                    case "24":
                        break;
                    case "25":
                        break;
                    case "26":
                        break;
                    case "27":
                        break;
                    case "28":
                        break;
                    case "29":
                        break;
                    case "30":
                        break;
                    case "31":
                        break;
                    case "32":
                        break;
                    case "33":
                        break;
                    case "34":
                        break;
                    case "35":
                        break;
                    case "36":
                        break;
                    default:
                        sendErrorMessage(e.getAuthor().getAsMention() + " '" + args[1] + "' is not an valid bet option.");
                        sendUsageMessage();
                        break;
                }
            }
        }else{
            sendUsageMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Play Roulette to win some extra money.";
    }

    @Override
    public String getUsage() {
        return "roulette <money> <bet option>" ;
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
