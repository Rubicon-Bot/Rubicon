package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.core.minigames.RouletteNumber;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
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
                if(user_set_money == 0){
                    sendErrorMessage(e.getAuthor().getAsMention() + " you have to bet more than 0 Ruby's!");
                    return;
                }
            } catch (NumberFormatException exception) {
                return;
            }
            if (Integer.parseInt(user_has_money) < user_set_money) {
                sendErrorMessage(e.getAuthor().getAsMention() + " You don't have enough Ruby. Your entry wasn't accepted.");
            }else{
                int RandomRouletteNumber = ThreadLocalRandom.current().nextInt(0, 36 + 1);
                switch (args[1]){
                    case "red":
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendEmbededMessage("'Rien ne va plus!' The Number is " + RandomRouletteNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[RandomRouletteNumber] + ".");
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
                        sendHelpMessage(e);
                        break;
                }
            }
        }else{
            sendHelpMessage(e);
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

    private void sendHelpMessage(MessageReceivedEvent event) {
           event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(22, 138, 233))
                        .setDescription("__**BETTING OPTIONS FOR ROULETTE**__\n`" + "roulette <money> <bet option>`\n\n\n")

                        .addField("Winning multiplicator: x2",
                                "`<bet option>:`  **even** - All numbers that are winning with this option:\n" +
                                        "> 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36 <\n\n"+
                                        "`<bet option>:`  **odd** - All numbers that are winning with this option:\n" +
                                        "> 1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35 <\n\n" +
                                        "`<bet option>:`  **red** - All numbers that are winning with this option:\n" +
                                        "> 1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36 <\n\n" +
                                        "`<bet option>:`  **black** - All numbers that are winning with this option:\n" +
                                        "> 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35 <\n\n" +
                                        "`<bet option>:`  **1-18** - All numbers that are winning with this option:\n" +
                                        "> 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 <\n\n" +
                                        "`<bet option>:`  **19-36** - All numbers that are winning with this option:\n" +
                                        "> 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 <\n" +
                                        ":heavy_minus_sign: :heavy_minus_sign: :heavy_minus_sign: ",  false)

                        .addField("Winning multiplicator: x3",
                                "`<bet option>:`  **1-12** - All numbers that are winning with this option:\n" +
                                        "> 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 <\n\n"+
                                        "`<bet option>:`  **13-24** - All numbers that are winning with this option:\n" +
                                        "> 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 <\n\n" +
                                        "`<bet option>:`  **25-36** - All numbers that are winning with this option:\n" +
                                        "> 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 <\n\n" +
                                        "`<bet option>:`  **column_low** - All numbers that are winning with this option:\n" +
                                        "> 1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34 <\n\n" +
                                        "`<bet option>:`  **column_mid** - All numbers that are winning with this option:\n" +
                                        "> 2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35 <\n\n" +
                                        "`<bet option>:`  **column_up** - All numbers that are winning with this option:\n" +
                                        "> 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36 <\n" +
                                        ":heavy_minus_sign: :heavy_minus_sign: :heavy_minus_sign: ",  false)

                        .addField("Winning multiplicator: x36",
                                "`<bet option>:`  **#** - (# = single number) Only the number you bet on, can win!\n",  false)

                        .build()
        ).queue();
    }
}