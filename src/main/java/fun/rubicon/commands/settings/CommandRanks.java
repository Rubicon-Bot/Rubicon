package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandRanks extends CommandHandler {
    public CommandRanks() {
        super(new String[]{"ranks", "rank"}, CommandCategory.SETTINGS, new PermissionRequirements("ranks", false, true), "Get ranks on your server", "<role1> <role2> <role3> \ncreate\ndelete\nlist");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        RubiconGuild guild = RubiconGuild.fromGuild(invocation.getGuild());
        String[] args = invocation.getArgs();
        Message message = invocation.getMessage();
        Member member = invocation.getMember();
        if (args.length == 0)
            return createHelpMessage();
        guild.checkRanks();
        switch (args[0]) {
            case "create":
            case "add":
                if (!invocation.getMember().hasPermission(Permission.MANAGE_ROLES))
                    return message(error(invocation.translate("command.ranks.noperm.title"), invocation.translate("command.ranks.noperm.user.description")));
                if (!invocation.getSelfMember().hasPermission(Permission.MANAGE_ROLES))
                    return message(error(invocation.translate("command.ranks.noperm.title"), invocation.translate("command.ranks.noperm.bot.description")));
                Role rank = guild.getGuild().getController().createRole().setName(args[1]).setMentionable(true).complete();
                /* Whitelist role as rank */
                guild.allowRank(rank);
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.ranks.created.title"), String.format(invocation.translate("command.ranks.created.description"), rank.getName()))));
                break;
            case "remove":
            case "delete":
                if (message.getMentionedRoles().isEmpty())
                    return message(error(invocation.translate("command.ranks.nomention.title"), invocation.translate("command.ranks.nomention.bot.description")));
                Role role = message.getMentionedRoles().get(0);
                if (!guild.isRank(role))
                    return message(error(invocation.translate("command.ranks.notarank.title"), invocation.translate("command.ranks.notarank.description")));
                /* Remove role from rank whitelist */
                guild.disallowRank(role);
                role.delete().queue();
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.ranks.deleted.title"), invocation.translate("command.ranks.deleted.description"))));
                break;
            case "list":
                if (!guild.useRanks())
                    return message(error(invocation.translate("command.ranks.disabled.title"), invocation.translate("command.ranks.disabled.description")));
                StringBuilder ranks = new StringBuilder();
                guild.getRanks().forEach(r -> ranks.append("`").append(r.getName()).append("`").append(", "));
                ranks.replace(ranks.lastIndexOf(","), ranks.lastIndexOf(",") + 1, "");
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.ranks.list.title"), ranks.toString())));
                break;
            default:
                if (!guild.useRanks())
                    return message(error(invocation.translate("command.ranks.disabled.title"), invocation.translate("command.ranks.disabled.description")));
                List<Role> toAddRoles = new ArrayList<>();
                List<Role> toRemoveRoles = new ArrayList<>();
                StringBuilder addedRoles = new StringBuilder();
                StringBuilder removedRoles = new StringBuilder();
                Arrays.asList(args).forEach(name -> {
                    if (guild.getGuild().getRolesByName(name, true).isEmpty()) return;
                    Role rankRole = guild.getGuild().getRolesByName(name, true).get(0);
                    if (member.getRoles().contains(rankRole)) {
                        toRemoveRoles.add(rankRole);
                        removedRoles.append(rankRole.getName()).append(", ");
                    } else {
                        toAddRoles.add(rankRole);
                        addedRoles.append(rankRole.getName()).append(", ");
                    }
                });
                EmbedBuilder emb = new EmbedBuilder()
                        .setColor(Colors.COLOR_PRIMARY)
                        .setTitle(invocation.translate("command.ranks.embed.title"));
                if(!toRemoveRoles.isEmpty()){
                    guild.getGuild().getController().removeRolesFromMember(member, toRemoveRoles).queue();
                    removedRoles.replace(removedRoles.lastIndexOf(","), removedRoles.lastIndexOf(",") + 1, "");
                    emb.addField(invocation.translate("command.ranks.embed.removed"), removedRoles.toString(), false);
                }
                if(!toAddRoles.isEmpty()){
                    guild.getGuild().getController().addRolesToMember(member, toAddRoles).queue();
                    addedRoles.replace(addedRoles.lastIndexOf(","), addedRoles.lastIndexOf(",") + 1, "");
                    emb.addField(invocation.translate("command.ranks.embed.added"), addedRoles.toString(), false);
                }
                SafeMessage.sendMessage(invocation.getTextChannel(), message(emb));
        }
        return null;
    }
}
