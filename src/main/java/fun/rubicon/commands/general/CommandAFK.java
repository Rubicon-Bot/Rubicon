/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandAFK extends CommandHandler {

    public CommandAFK() {
        super(new String[]{"afk"}, CommandCategory.GENERAL, new PermissionRequirements("afk", false, true), "Enable/Disable your AFK state. That will inform other members if you are AFK or not.", "<text> | Sets your AFK state.\ndisable | Disables your AFK state.");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0) {
            RubiconUser rubiconUser = RubiconUser.fromUser(invocation.getAuthor());

            return message(info(invocation.translate("command.afk.current"), (rubiconUser.getAFKState()).equals("") ? invocation.translate("command.afk.disable.error.title") : rubiconUser.getAFKState()).setFooter(invocation.getAuthor().getName(), invocation.getAuthor().getEffectiveAvatarUrl()));
        } else if (invocation.getArgs().length == 1) {
            if (invocation.getArgs()[0].equalsIgnoreCase("disable")) {
                return disable(invocation);
            } else {
                return enable(invocation);
            }
        } else {
            return enable(invocation);
        }
    }

    private Message enable(CommandManager.ParsedCommandInvocation invocation) {
        RubiconMember rubiconMember = RubiconMember.fromMember(invocation.getMember());
        String afkText = invocation.getArgsString();
        rubiconMember.setAFKState(afkText);
        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.afk.enable.success.title") + "!", invocation.translate("command.afk.enable.success.description").replaceAll("%message%", "`" + afkText + "`.")));
    }

    private Message disable(CommandManager.ParsedCommandInvocation invocation) {
        RubiconMember rubiconMember = RubiconMember.fromMember(invocation.getMember());
        if (rubiconMember.isAFK()) {
            rubiconMember.setAFKState("");
            return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.afk.disable.success.title") + "!", invocation.translate("command.afk.disable.success.description")));
        } else {
            return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.afk.disable.error.title") + "!", invocation.translate("command.afk.disable.error.description") + "."));
        }
    }
}
