/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandPrefix extends CommandHandler {

    public CommandPrefix() {
        super(new String[]{"prefix"}, CommandCategory.SETTINGS, new PermissionRequirements("prefix", false, false), "Change your custom server prefix.", "set <new prefix>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(invocation.getGuild());
        if (invocation.getArgs().length <= 1)
            return createHelpMessage();

        String subCommand = invocation.getArgs()[0];
        if (!subCommand.equalsIgnoreCase("set")) {
            return createHelpMessage();
        }

        String newPrefix = invocation.getArgs()[1];
        if (newPrefix.length() > 5)
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.prefix.set.error.title") + "!", invocation.translate("command.prefix.set.error.description") + "."));
        rubiconGuild.setPrefix(newPrefix);
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.prefix.set.success.title") + "!", invocation.translate("command.prefix.set.success.description").replaceAll("%prefix%", "`" + newPrefix + "`")));
    }
}
