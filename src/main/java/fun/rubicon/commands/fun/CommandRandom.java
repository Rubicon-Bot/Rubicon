/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.fun;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandRandom extends CommandHandler {

    public CommandRandom() {
        super(new String[]{"random", "rand"}, CommandCategory.FUN, new PermissionRequirements("random", false, true), "Generates a random digit.", "<min> <max>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length != 2)
            return createHelpMessage();

        if (!StringUtil.isNumeric(invocation.getArgs()[0])) {
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.random.error.numeric.title"), invocation.translate("command.random.error.numeric.description")));
        }
        if (!StringUtil.isNumeric(invocation.getArgs()[1])) {
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.random.error.numeric.title"), invocation.translate("command.random.error.numeric.description")));
        }
        int iArgs[] = {Integer.parseInt(invocation.getArgs()[0]), Integer.parseInt(invocation.getArgs()[1])};
        int rand = generateRandom(iArgs[0], iArgs[1]);
        return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.random.success.title"), "").setDescription(invocation.translate("command.random.success.description").replace("%digit%", rand + "")));
    }

    public static int generateRandom(int min, int max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(min, max + 1);
    }
}
