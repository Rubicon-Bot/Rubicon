package fun.rubicon.commands.general;


import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.PriceList;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.Date;
import java.util.List;

/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */
public class CommandPremium extends CommandHandler {

    public static final long PREMIUM_TIME = new Date().getTime() + 15638400000L;

    public CommandPremium() {
        super(new String[]{"premium"}, CommandCategory.GENERAL, new PermissionRequirements("premium", false, true), "See your premium state or buy premium.", "| Shows current premium state");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        RubiconUser author = RubiconUser.fromUser(invocation.getAuthor());
        if (args.length == 0) {
            EmbedBuilder em = new EmbedBuilder();
            em.setAuthor(
                    invocation.getAuthor().getName(), null, invocation.getAuthor().getAvatarUrl()
            );
            em.setColor(
                    Colors.COLOR_SECONDARY
            );
            if ((author.isPremium())) {
                em.setDescription(invocation.translate("command.premium.until") + " " + author.formatExpiryDate().replace("%", ""));
            } else {
                em.setDescription(invocation.translate("command.premium.nopremium"));
            }
            return message(em);
        }

        switch (args[0].toLowerCase()) {
            default:
                return createHelpMessage();
            case "add":
                if (userPermissions.isBotAuthor()) {
                    List<User> users = invocation.getMessage().getMentionedUsers();
                    if (users.size() == 0) {
                        return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.premium.modify.error.title"), invocation.translate("command.premium.modify.error.desc")));
                    } else {
                        StringBuilder successValue = new StringBuilder();
                        successValue.append(invocation.translate("command.premium.modify.success.add"));
                        for (User user : users) {
                            RubiconUser pUser = RubiconUser.fromUser(user);
                            pUser.setPremium(PREMIUM_TIME);
                            assignPremiumRole(pUser);
                            successValue.append(" / ").append(user.getAsMention());
                        }
                        String successText = successValue.toString().replaceFirst(" /", "");
                        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.premium.success"), successText + "."));
                    }
                }
            case "remove":
                if (userPermissions.isBotAuthor()) {
                    List<User> users = invocation.getMessage().getMentionedUsers();
                    if (users.size() == 0) {
                        return EmbedUtil.message(EmbedUtil.error(invocation.translate("command.premium.modify.error.title"), invocation.translate("command.premium.modify.error.desc")));
                    } else {
                        StringBuilder successValue = new StringBuilder();
                        successValue.append(invocation.translate("command.premium.modify.success.remove"));
                        for (User user : users) {
                            RubiconUser pUser = RubiconUser.fromUser(user);
                            pUser.setPremium(0);
                            assignPremiumRole(pUser);
                            successValue.append(" / ").append(user.getAsMention());
                        }
                        String successText = successValue.toString().replaceFirst(" /", "");
                        return EmbedUtil.message(EmbedUtil.success(invocation.translate("command.premium.success"), successText + "."));
                    }
                }
        }
        return createHelpMessage();
    }

    private void assignPremiumRole(RubiconUser user) {
        if (!user.isPremium()) return;
        if (RubiconBot.getSelfUser().getIdLong() != 380713705073147915L)
            return;
        Guild guild = RubiconBot.getShardManager().getGuildById(Info.RUBICON_SERVER);
        if (guild.getMember(user.getUser()) == null) return;
        Role role = guild.getRoleById(382160159339970560L);
        if (role == null)
            return;
        guild.getController().addSingleRoleToMember(guild.getMember(user.getUser()), role).queue();
    }

}