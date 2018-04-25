/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandInvite extends CommandHandler {

    public CommandInvite() {
        super(new String[]{"invite", "inv"}, CommandCategory.GENERAL, new PermissionRequirements("invite", false, true), "Sends you a nice bot and support-server invite.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        return EmbedUtil.message(EmbedUtil.info(invocation.translate("command.invite.title"), "[" + invocation.translate("command.invite.description.1") + "](http://inv.rucb.co)\n[" + invocation.translate("command.invite.description.2") + "](http://dc.rucb.co)"));
    }
}
