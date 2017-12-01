package fun.rubicon.commands.fun;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.core.minigames.SlotMachine;
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
public class CommandSlot extends Command {
    public CommandSlot(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) throws ParseException {
        int slot_one = 0;
        int slot_two = 0;
        int slot_three = 0;
        int payed_money = 0;
        int user_set_money = 0;
        int multiplicator = 0;
        int user_has_money = Integer.parseInt(RubiconBot.getMySQL().getUserValue(e.getAuthor(),"money"));

        if(args.length  >= 1){
            try{
                payed_money =Integer.parseInt(args[0]);
            }catch(NumberFormatException exception){
                sendErrorMessage("The value is not a number!");
                sendHelpMessage(e);
                return;
            }
            if(payed_money <= user_has_money && payed_money > 0){
                slot_one = ThreadLocalRandom.current().nextInt(0, SlotMachine.Slots.length);
                slot_two = ThreadLocalRandom.current().nextInt(0, SlotMachine.Slots.length);
                slot_three = ThreadLocalRandom.current().nextInt(0, SlotMachine.Slots.length);
                sendEmbededMessage("The slot machine rolled: " + SlotMachine.Slots[slot_one] + "  " + SlotMachine.Slots[slot_two] + "  " + SlotMachine.Slots[slot_three]);
                if(slot_one == slot_two && slot_one == slot_three){
                    if(slot_one <= 7){
                        multiplicator = 2;
                    }else if(slot_one <= 11){
                        multiplicator = 3;
                    }else if(slot_one <= 13){
                        multiplicator = 5;
                    }else{
                        multiplicator = 8;
                    }
                    sendEmbededMessage("Concratulation! " +e.getAuthor().getAsMention() +" You won " + (payed_money*multiplicator) + " Ruby's. :tada:");
                    user_set_money = user_has_money + (payed_money*multiplicator);
                    RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(user_set_money));
                }else{
                    sendEmbededMessage("Sorry. " + e.getAuthor().getAsMention() + " you lose. :cry: More luck next time!");
                    user_set_money = user_has_money - payed_money;
                    RubiconBot.getMySQL().updateUserValue(e.getAuthor(),"money",String.valueOf(user_set_money));
                }
            }else{
                if(payed_money == 0){
                    sendErrorMessage(e.getAuthor().getAsMention() + " you have to pay Ruby's to play at a slot machine.");
                    return;
                }
                sendErrorMessage(e.getAuthor().getAsMention() + " you don't have enough Ruby's. You only have " + user_has_money + " Ruby's.");
                return;
            }
        }else{
            sendErrorMessage(e.getAuthor().getAsMention() + " you need to pay Ruby's to play at a slot machine.");
            sendHelpMessage(e);
        }
    }

    @Override
    public String getDescription() {
        return "Play a short round on a slot machine. Winning only at three of a kind!";
    }

    @Override
    public String getUsage() {
        return "slot <money>";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }

    private void sendHelpMessage(MessageReceivedEvent event) {
        event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(22, 138, 233))
                        .setDescription("__**WINNING OPTIONS FOR SLOT MACHINE**__\n`" + "slot <money> - 3 of a kind wins!`\n\n\n")

                        .addField("Winning multiplicator: x2",
                                ":bell: :football: :soccer: :8ball: :green_apple: :lemon: :strawberry: :watermelon:",  false)

                        .addField("Winning multiplicator: x3",
                                ":heart: :yellow_heart: :blue_heart: :green_heart:",  false)

                        .addField("Winning multiplicator: x5",
                                ":star2: :zap:",  false)

                      /*  .addField("Winning multiplicator: x8",
                                ">>HERE CODE FOR RUBY<<",  false)
                      */
                        .build()
        ).queue();
    }

}
