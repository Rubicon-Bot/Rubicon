/*
 *Copyright (c) 2018  Rubicon Bot Development Team
 *Licensed under the GPL-3.0 license.
 *The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;


import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import static fun.rubicon.util.EmbedUtil.error;
import static fun.rubicon.util.EmbedUtil.message;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license GPL-3.0 License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.general
 */
public class CommandMoney extends CommandHandler {

    public CommandMoney() {
        super(new String[]{}, CommandCategory.GENERAL, new PermissionRequirements("money", false, true), "", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconUser user = RubiconUser.fromUser(invocation.getAuthor());
        RubiconUser user2 = null;

        if (invocation.getMessage().getMentionedUsers().size() == 1) {
            user2 = RubiconUser.fromUser(invocation.getMessage().getMentionedUsers().get(0));
        }

        int user1_has_money = 0;
        int user2_has_money = 0;
        int user_spend_money = 0;

        if (invocation.getArgs().length == 0) {
            SafeMessage.sendMessage(invocation.getTextChannel(), new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setDescription(invocation.translate("command.money.balance") + "`" + String.valueOf(user.getMoney())).setAuthor(invocation.getAuthor().getName() + invocation.translate("command.money.balance.user"), null, invocation.getAuthor().getAvatarUrl()).build());
            return null;
        }

        switch (invocation.getArgs()[0]) {
            case "give":
                if (invocation.getArgs().length == 3) {
                    try {
                        if (invocation.getMessage().getMentionedMembers().size() == 1) {
                            if (invocation.getMessage().getMentionedMembers().get(0).getUser().getId().equalsIgnoreCase(invocation.getAuthor().getId())) {
                                return new MessageBuilder().setEmbed(error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.money.give.selferror.desc")).build()).build();
                            }
                        }else {
                            return createHelpMessage();
                        }
                        user_spend_money = Integer.parseInt(invocation.getArgs()[invocation.getArgs().length - 1]);
                        user1_has_money = user.getMoney();
                        user2_has_money = user2.getMoney();
                        if (user1_has_money < user_spend_money) {
                            SafeMessage.sendMessage(invocation.getTextChannel(), error(invocation.translate("command.money.give.notmoney.title"), invocation.translate("command.money.give.notmoney.des1") + invocation.getMessage().getAuthor().getAsMention() + invocation.translate("command.money.give.notmoney.des2") + user1_has_money + invocation.translate("command.money.give.notmoney.des3")).build());
                            return null;
                        } else {
                            if ((user2_has_money + user_spend_money) <= 2147483647) {
                                user.setMoney(user1_has_money - user_spend_money);
                                user2.setMoney(user2_has_money + user_spend_money);
                                SafeMessage.sendMessage(invocation.getTextChannel(),EmbedUtil.success(invocation.translate("command.money.give.suc.title"), invocation.getMessage().getAuthor().getAsMention() + invocation.translate("command.money.give.suc.des1") + user_spend_money + invocation.translate("command.money.give.suc.des2") + invocation.getMessage().getMentionedUsers().get(0).getAsMention() + ".").build());
                            } else {
                                return message(error("Money value to big!", "Money value must be smaller than " + ((2147483647 - user2_has_money) + 1) + "!"));
                            }
                        }
                    } catch (NumberFormatException e) {
                        SafeMessage.sendMessage(invocation.getTextChannel(), invocation.translate("command.money.give.numbertoobig"));
                        return null;
                    }
                }

        }


        return null;
    }
}