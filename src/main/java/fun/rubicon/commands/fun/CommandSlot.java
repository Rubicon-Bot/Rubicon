/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.minigames.SlotMachine;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static fun.rubicon.util.EmbedUtil.*;

public class CommandSlot extends CommandHandler {

    public CommandSlot() {
        super(new String[]{"slot", "slots"}, CommandCategory.FUN,
                new PermissionRequirements("command.slot", false, true),
                "Play a short round on a slot machine. Winning only at three of a kind!", "<money>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        UserSQL userSQL = new UserSQL(parsedCommandInvocation.getAuthor());
        int slot_one = 0;
        int slot_two = 0;
        int slot_three = 0;
        int payed_money = 0;
        int user_set_money = 0;
        int multiplicator = 0;
        int user_has_money = Integer.parseInt(userSQL.get("money"));

        if (parsedCommandInvocation.getArgs().length >= 1) {
            try {
                payed_money = Integer.parseInt(parsedCommandInvocation.getArgs()[0]);
            } catch (NumberFormatException exception) {
                sendHelpMessage(parsedCommandInvocation.getMessage().getChannel());
                return message(error("Invalid argument", "Your bet must be an integer number."));
            }
            if (payed_money <= user_has_money && payed_money > 0) {
                slot_one = ThreadLocalRandom.current().nextInt(0, SlotMachine.Slots.length);
                slot_two = ThreadLocalRandom.current().nextInt(0, SlotMachine.Slots.length);
                slot_three = ThreadLocalRandom.current().nextInt(0, SlotMachine.Slots.length);
                parsedCommandInvocation.getMessage().getChannel().sendMessage(message(info("The slot machine rolled!",
                        "The slot machine rolled: " + SlotMachine.Slots[slot_one] + "  " + SlotMachine.Slots[slot_two] + "  " + SlotMachine.Slots[slot_three]))).queue();
                if (slot_one == slot_two && slot_one == slot_three) {
                    if (slot_one <= 7) {
                        multiplicator = 2;
                    } else if (slot_one <= 11) {
                        multiplicator = 3;
                    } else if (slot_one <= 13) {
                        multiplicator = 5;
                    } else {
                        multiplicator = 8;
                    }
                    user_set_money = user_has_money + (payed_money * multiplicator);
                    userSQL.set("money", String.valueOf(user_set_money));
                    return message(embed("You win!",
                            "Congratulations! " + parsedCommandInvocation.getMessage().getAuthor().getAsMention()
                                    + " You won " + (payed_money * multiplicator) + " Ruby's. :tada:").setColor(Colors.COLOR_SECONDARY));
                } else {
                    user_set_money = user_has_money - payed_money;
                    userSQL.set("money", String.valueOf(user_set_money));
                    return message(embed("You lose!", "Sorry. " + parsedCommandInvocation.getMessage().getAuthor().getAsMention()
                            + " you lose. :cry: More luck next time!").setColor(Colors.COLOR_SECONDARY));
                }
            } else {
                if (payed_money == 0) {
                    return message(error("You didn't pay", parsedCommandInvocation.getMessage().getAuthor().getAsMention()
                            + " you have to pay Ruby's to play at a slot machine."));
                }
                return message(error("Not enough money", parsedCommandInvocation.getMessage().getAuthor().getAsMention()
                        + " you don't have enough Ruby's. You only have " + user_has_money + " Ruby's."));
            }
        }
        sendHelpMessage(parsedCommandInvocation.getMessage().getChannel());
        return message(error("You didn't pay", parsedCommandInvocation.getMessage().getAuthor().getAsMention()
                + " you have to pay Ruby's to play at a slot machine."));
    }

    private void sendHelpMessage(MessageChannel channel) {
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(new Color(22, 138, 233))
                        .setDescription("__**WINNING OPTIONS FOR SLOT MACHINE**__\n`" + "slot <money> - 3 of a kind wins!`\n\n\n")

                        .addField("Winning multiplicator: x2",
                                ":bell: :football: :soccer: :8ball: :green_apple: :lemon: :strawberry: :watermelon:", false)

                        .addField("Winning multiplicator: x3",
                                ":heart: :yellow_heart: :blue_heart: :green_heart:", false)

                        .addField("Winning multiplicator: x5",
                                ":star2: :zap:", false)

                        .addField("Winning multiplicator: x8",
                                ":diamonds:", false)

                        .build()
        ).queue();
    }

}
