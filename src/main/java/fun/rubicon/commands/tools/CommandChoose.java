/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.message;
import static fun.rubicon.util.EmbedUtil.success;

/**
 * Handles the 'choose' command which randomly chooses a provided option.
 *
 * @author xEiisKeksx, tr808axm
 */
public class CommandChoose extends CommandHandler {
    /**
     * Constructs the 'choose' command handler.
     */
    public CommandChoose() {
        super(new String[]{"choose", "choose-option", "choose-random"}, CommandCategory.TOOLS,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.choose"),
                "Randomly chooses one of you options.", "<option-1> <option-2> [option-3] [option-...]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions permissions) {
        if (invocation.args.length == 0)
            return createHelpMessage();
        else {
            EmbedBuilder embedBuilder = success("Chose an option", "The option of choice is `"
                    + invocation.args[(int) (Math.random() * invocation.args.length)] + "`.");

            if (invocation.args.length == 1)
                embedBuilder.setFooter("This was kind of obvious, wasn't it?", null);

            return message(embedBuilder);
        }
    }
}
