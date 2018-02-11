/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.*;

/**
 * Handles the 'dice' command with which the user can roll a dice.
 */
public class CommandDice extends CommandHandler {
    /**
     * Constructs the 'dice' command handler.
     */
    public CommandDice() {
        super(new String[]{"dice", "roll", "roll-dice"}, CommandCategory.TOOLS,
                new PermissionRequirements("command.dice", false, true),
                "Roll a dice.", "<minimum-value> <maximum value>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        if (invocation.getArgs().length < 2)
            return createHelpMessage(invocation);
        else {
            int min, max;
            // parse values
            try {
                min = Integer.parseInt(invocation.getArgs()[0]);
            } catch (NumberFormatException e) {
                return message(error("Invalid value", "The minimum value `" + invocation.getArgs()[0]
                        + "` is not an integer number."));
            }
            try {
                max = Integer.parseInt(invocation.getArgs()[1]);
            } catch (NumberFormatException e) {
                return message(error("Invalid value", "The maximum value `" + invocation.getArgs()[1]
                        + "` is not an integer number."));
            }

            // check if max is greater than min
            if (min > max)
                return message(error("Invalid values", "The maximum value must be greater than the " +
                        "minimum value!"));

            // result
            return message(embed(":game_die: " + "The dice rolled...",
                    invocation.getMessage().getAuthor().getAsMention() + " rolled a `"
                            + ((int) (min + Math.random() * (max - min + 1))) + "`!"));
        }
    }
}
