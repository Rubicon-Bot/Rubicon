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
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;


import static fun.rubicon.util.EmbedUtil.*;

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
            return EmbedUtil.message(EmbedUtil.info("Rubys", String.format("%d Rubys", rubiconMember.getMoney())).setFooter("Requested by " + invocation.getAuthor().getName(), null));
        }
        return null;
    }
}