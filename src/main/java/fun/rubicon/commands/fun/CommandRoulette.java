/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.minigames.RouletteNumber;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.UserSQL;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Handles the 'roulette' command with that users can play roulette.
 *
 * @author xEiisKeksx, tr808axm
 */
public class CommandRoulette extends CommandHandler {
    /**
     * Constructs the 'roulette' command handler.
     */
    public CommandRoulette() {
        super(new String[]{"roulette", "roulete", "rulette", "roullete"}, CommandCategory.FUN,
                new PermissionRequirements("command.roulette", false, false),
                "Play Roulette to win some extra rubys.", "<bet-amount> <bet-option>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        UserSQL userSQL = new UserSQL(invocation.getAuthor());
        if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);
        else {
            int betAmount;
            // parse bet amount
            try {
                betAmount = Integer.parseInt(invocation.getArgs()[0]);
            } catch (NumberFormatException e) {
                return message(error("Invalid value", "The minimum value `" + invocation.getArgs()[0]
                        + "` is not an integer number."));
            }

            // check if bet amount is > 0
            if (betAmount <= 0)
                return message(error("Bet too little", "You need to bet at least 1 ruby to play Roulette."));

            // check if the user has enough money to cover his bet
            if (betAmount > Integer.parseInt(userSQL.get("money")))
                return message(error("Not enough money", "You don't have `" + betAmount + "` rubys. " +
                        "Check your money with `" + invocation.getPrefix() + "money`."));

            // already generate random number
            String betOption = invocation.getArgs()[1];
            int rolledNumber = (int) (Math.random() * 37); // 0 - 36
            int multiplier;
            boolean wins;
            switch (betOption) {
                case "even":
                    multiplier = 2;
                    wins = rolledNumber != 0 && rolledNumber % 2 == 0;
                    break;
                case "odd":
                    multiplier = 2;
                    wins = rolledNumber % 2 != 0;
                    break;
                case "red":
                    multiplier = 2;
                    wins = RouletteNumber.RouletteColor[rolledNumber].equals("red");
                    break;
                case "black":
                    multiplier = 2;
                    wins = RouletteNumber.RouletteColor[rolledNumber].equals("black");
                    break;
                case "1-18":
                    multiplier = 2;
                    wins = rolledNumber > 0 && rolledNumber < 19;
                    break;
                case "19-36":
                    multiplier = 2;
                    wins = rolledNumber > 18; // numbers can not be greater than 36
                    break;
                case "1-12":
                    multiplier = 3;
                    wins = rolledNumber > 0 && rolledNumber < 13;
                    break;
                case "13-24":
                    multiplier = 3;
                    wins = rolledNumber > 12 && rolledNumber < 25;
                    break;
                case "25-36":
                    multiplier = 3;
                    wins = rolledNumber > 24;
                    break;
                case "column_low":
                    multiplier = 3;
                    wins = rolledNumber % 3 == 1;
                    break;
                case "column_mid":
                    multiplier = 3;
                    wins = rolledNumber % 3 == 2;
                    break;
                case "column_up":
                    multiplier = 3;
                    wins = rolledNumber != 0 && rolledNumber % 3 == 0;
                    break;
                default:
                    int betNumber;
                    try {
                        betNumber = Integer.parseInt(betOption);
                        if (betNumber < 0 || betNumber > 36)
                            throw new IllegalArgumentException();
                    } catch (IllegalArgumentException e) { // NumberFormatException is an IllegalArgumentException
                        return message(error("Invalid bet option", "You can not bid on `" + betOption
                                + "`. Use `" + invocation.getPrefix() + invocation.getCommandInvocation() + "` for a full bet option list."));
                    }
                    multiplier = 36;
                    wins = betNumber == rolledNumber;
                    break;
            }
            sendAndDeleteOnGuilds(invocation.getMessage().getChannel(), message(info("Rien ne va plus!",
                    "The number is " + rolledNumber + ".\nIt's color is " + RouletteNumber.RouletteColor[rolledNumber] + ".")));
            if (wins) {
                int wonMoney = betAmount * (multiplier - 1);
                // update money
                userSQL.set("money", String.valueOf(Integer.parseInt(userSQL.get("money")) + wonMoney));
                // respond
                return message(embed(":star: You win", "Congratulations "
                        + invocation.getMessage().getAuthor().getAsMention() + "! You won " + wonMoney + " rubys.")
                        .setColor(Color.YELLOW));
            } else {
                // update money
                userSQL.set("money", String.valueOf(Integer.parseInt(userSQL.get("money")) - betAmount));
                // respond
                return message(embed(":cry: You lose", "Sorry, no luck this time!"));
            }
        }
    }

    @Override
    public Message createHelpMessage(String serverPrefix, String aliasToUse) {
        String x2betOptions =
                "`even`: Bet on even numbers.\n" +
                        "`2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36`\n" +
                        "\n" +
                        "`odd`: Bet on odd numbers.\n" +
                        "`1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35`\n" +
                        "\n" +
                        "`red`: Bet on red numbers.\n" +
                        "`1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36`\n" +
                        "\n" +
                        "`black`: Bet on black numbers.\n" +
                        "`2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35`\n" +
                        "\n" +
                        "`1-18`: Bet on the first half of the numbers.\n" +
                        "`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18`\n" +
                        "\n" +
                        "`19-36`: Bet on the second half of the numbers.\n",
                x3betOptions =
                        "`1-12`: Bet on the first third of the numbers.\n" +
                                "`1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12`\n" +
                                "\n" +
                                "`13-24`: Bet on the second third of the numbers.\n" +
                                "`13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24`\n" +
                                "\n" +
                                "`25-36`: Bet on the third third of the numbers.\n" +
                                "`25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36`\n" +
                                "\n" +
                                "`column_low`: Bet on the left column.\n" +
                                "`1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34`\n" +
                                "\n" +
                                "`column_mid`: Bet on the middle column.\n" +
                                "`2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35`\n" +
                                "\n" +
                                "`column_up`: Bet on the right column.\n" +
                                "`3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36`\n",
                x36betOptions =
                        "`#`: Bet on any single number.\n" +
                                "Only your bet wins`#`\n" +
                                "\n";

        return message(info('\'' + aliasToUse + "' command help", getDescription())
                .addField("Aliases", String.join(", ", getInvocationAliases()), false)
                .addField("Usage", serverPrefix + aliasToUse + ' ' + getParameterUsage(), false)
                .addField("Bet options (multiplier x2)", x2betOptions, false)
                .addField("Bet options (multiplier x3)", x3betOptions, false)
                .addField("Bet options (multiplier x36)", x36betOptions, false));
    }
}
