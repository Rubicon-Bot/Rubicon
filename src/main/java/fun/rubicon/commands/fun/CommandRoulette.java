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
        int column_up[]={3,6,9,12,15,18,21,24,27,30,33,36};
        int column_mid[]={2,5,8,11,14,17,20,23,26,29,32,35};
        int column_down[]={1,4,7,10,13,16,19,22,25,28,31,34};
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
                sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        if(Integer.valueOf(RandomRouletteNumber) %2 == 0){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *2) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*2);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "odd":
                        if(Integer.valueOf(RandomRouletteNumber) %2 != 0){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *2) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*2);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "1-18":
                        if(Integer.valueOf(RandomRouletteNumber) <= 18 && Integer.valueOf(RandomRouletteNumber) > 0){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *2) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*2);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "19-36":
                        if(Integer.valueOf(RandomRouletteNumber) <= 36 && Integer.valueOf(RandomRouletteNumber) > 18){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *2) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*2);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "1-12":
                        if(Integer.valueOf(RandomRouletteNumber) <= 12 && Integer.valueOf(RandomRouletteNumber) > 0){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *3) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*3);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "13-24":
                        if(Integer.valueOf(RandomRouletteNumber) <= 24 && Integer.valueOf(RandomRouletteNumber) > 12){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *3) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*3);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "25-36":
                        if(Integer.valueOf(RandomRouletteNumber) <= 36 && Integer.valueOf(RandomRouletteNumber) > 24){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *3) + " Ruby. :tada:");
                            int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*3);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        }
                        break;
                    case "column_up":
                        for(int i = 0; i < column_up.length; i++){
                            if(RandomRouletteNumber == column_up[i]){
                                sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *3) + " Ruby. :tada:");
                                int new_user_has_money = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*3);
                                RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                                break;
                            }else{
                                //weiter prüfen
                            }
                        }
                        sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                        int new_user_has_money = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                        RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money));
                        break;
                    case "column_mid":
                        for(int i = 0; i < column_mid.length; i++){
                            if(RandomRouletteNumber == column_mid[i]){
                                sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *3) + " Ruby. :tada:");
                                int new_user_has_money0 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*3);
                                RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money0));
                                break;
                            }else{
                                //weiter prüfen
                            }
                        }
                        sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                        int new_user_has_money1 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                        RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money1));
                        break;
                    case "column_low":
                        for(int i = 0; i < column_down.length; i++){
                            if(RandomRouletteNumber == column_down[i]){
                                sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *3) + " Ruby. :tada:");
                                int new_user_has_money2 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*3);
                                RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money2));
                                break;
                            }else{
                                //weiter prüfen
                            }
                        }
                        sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                        int new_user_has_money3 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                        RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money3));
                        break;
                    case "0":
                        if(RandomRouletteNumber == 0){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "1":
                        if(RandomRouletteNumber == 1){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "2":
                        if(RandomRouletteNumber == 2){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "3":
                        if(RandomRouletteNumber == 3){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "4":
                        if(RandomRouletteNumber == 4){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "5":
                        if(RandomRouletteNumber == 5){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "6":
                        if(RandomRouletteNumber == 6){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "7":
                        if(RandomRouletteNumber == 7){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "8":
                        if(RandomRouletteNumber == 8){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "9":
                        if(RandomRouletteNumber == 9){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "10":
                        if(RandomRouletteNumber == 10){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "11":
                        if(RandomRouletteNumber == 11){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "12":
                        if(RandomRouletteNumber == 12){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "13":
                        if(RandomRouletteNumber == 13){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "14":
                        if(RandomRouletteNumber == 14){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "15":
                        if(RandomRouletteNumber == 15){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "16":
                        if(RandomRouletteNumber == 16){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "17":
                        if(RandomRouletteNumber == 17){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "18":
                        if(RandomRouletteNumber == 18){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "19":
                        if(RandomRouletteNumber == 19){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "20":
                        if(RandomRouletteNumber == 20){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "21":
                        if(RandomRouletteNumber == 21){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "22":
                        if(RandomRouletteNumber == 22){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "23":
                        if(RandomRouletteNumber == 23){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "24":
                        if(RandomRouletteNumber == 24){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "25":
                        if(RandomRouletteNumber == 25){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "26":
                        if(RandomRouletteNumber == 26){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "27":
                        if(RandomRouletteNumber == 27){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "28":
                        if(RandomRouletteNumber == 28){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "29":
                        if(RandomRouletteNumber == 29){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "30":
                        if(RandomRouletteNumber == 30){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "31":
                        if(RandomRouletteNumber == 31){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "32":
                        if(RandomRouletteNumber == 32){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "33":
                        if(RandomRouletteNumber == 33){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "34":
                        if(RandomRouletteNumber == 34){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "35":
                        if(RandomRouletteNumber == 35){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
                        break;
                    case "36":
                        if(RandomRouletteNumber == 36){
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Congratulation! You won " + (user_set_money *36) + " Ruby. :tada:");
                            int new_user_has_money4 = Integer.valueOf(user_has_money) + (Integer.valueOf(user_set_money)*36);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money4));
                        }else{
                            sendEmbededMessage(e.getAuthor().getAsMention() + " Sorry! You lose. :cry: More luck next time!");
                            int new_user_has_money5 = Integer.valueOf(user_has_money) - Integer.valueOf(user_set_money);
                            RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(new_user_has_money5));
                        }
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
