/*
 *Copyright (c) 2018  Rubicon Bot Development Team
 *Licensed under the GPL-3.0 license.
 *The full license text is available in the LICENSE file provided with this project.
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
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Leon Kappes / Lee
 */
public class CommandMoney extends CommandHandler {

    public CommandMoney() {
        super(new String[]{"money", "rubys"}, CommandCategory.GENERAL, new PermissionRequirements("money", false, true), "See your money amount.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconMember rubiconMember = RubiconMember.fromMember(invocation.getMember());

        if(invocation.getArgs().length == 0) {
            return EmbedUtil.message(EmbedUtil.info("Rubys", invocation.translate("command.money.info").replace("%amount%", String.valueOf(rubiconMember.getMoney()))));
        }
        PermissionRequirements ownerRequirements = new PermissionRequirements("money.modify", true, false);
        if(!ownerRequirements.coveredBy(userPermissions)) {
            return EmbedUtil.message(EmbedUtil.no_permissions());
        }

        if(invocation.getArgs().length < 3 || invocation.getMessage().getMentionedUsers().size() != 1) {
            return createHelpMessage();
        }

        if(!StringUtil.isNumeric(invocation.getArgs()[1])) {
            return EmbedUtil.message(EmbedUtil.error("", ""));
        }
        int amount = Integer.parseInt(invocation.getArgs()[1]);
        RubiconUser user = RubiconUser.fromUser(invocation.getMessage().getMentionedUsers().get(0));

        switch (invocation.getArgs()[0]) {
            case "add":
                user.addMoney(amount);
                return EmbedUtil.message(EmbedUtil.success("Money added!", String.format("Added `%d` rubys to %s", amount, user.getUser().getAsMention())));
            case "remove":
                user.removeMoney(amount);
                return EmbedUtil.message(EmbedUtil.success("Money removed!", String.format("Removed `%d` rubys of %s", amount, user.getUser().getAsMention())));
            case "set":
                user.setMoney(amount);
                return EmbedUtil.message(EmbedUtil.success("Money set!", String.format("Set rubys of %s to `%d`", user.getUser().getAsMention(), amount)));
        }
        return null;
    }
}