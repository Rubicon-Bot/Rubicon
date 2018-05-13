package fun.rubicon.commands.tools;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Leon Kappes / Lee
 * @copyright RubiconBot Dev Team 2018
 * @License GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandServerInfo extends CommandHandler {

    public CommandServerInfo() {
        super(new String[]{"serverinfo", "guildinfo", "guild"}, CommandCategory.TOOLS, new PermissionRequirements("serverinfo", false, true), "Returns some information about the current/an other server", "\n<Server Name>\n<Server Id>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        Guild guild = getGuild(invocation);
        if (guild == null)
            return message(error(invocation.translate("command.serverinfo.found"), invocation.translate("command.serverinfo.found.description")));
        StringBuilder rawRoles = new StringBuilder();
        guild.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "");
        EmbedBuilder serverInfo = new EmbedBuilder();
        serverInfo.setColor(Colors.COLOR_PRIMARY)
                .setTitle(":desktop: " + invocation.translate("command.serverinfo.title") + guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField(invocation.translate("command.serverinfo.id"), "`" + guild.getId() + "`", true)
                .addField(invocation.translate("command.serverinfo.name"), "`" + guild.getName() + "`", true)
                .addField(invocation.translate("command.serverinfo.region"), guild.getRegion().toString(), true)
                .addField(invocation.translate("command.serverinfo.member"), String.valueOf(guild.getMembers().size()), true)
                .addField(invocation.translate("command.serverinfo.text"), String.valueOf(guild.getTextChannels().size()), true)
                .addField(invocation.translate("command.serverinfo.voice"), String.valueOf(guild.getVoiceChannels().size()), true)
                .addField(invocation.translate("command.serverinfo.roles"), String.valueOf(guild.getRoles().size()) + "\n ```" + roles.toString() + "```", false)
                .addField(invocation.translate("command.serverinfo.owner"), guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), true)
                .addField(invocation.translate("command.serverinfo.creation"), DateUtil.formatDate(guild.getCreationTime(), invocation.translate("date.format")), true);
        if (hasIcon(guild)) {
            serverInfo.addField(invocation.translate("command.serverinfo.icon"), "[" + invocation.translate("click") + "](" + guild.getIconUrl() + ")", true);
        }
        return message(serverInfo);

    }


    private Guild getGuild(CommandManager.ParsedCommandInvocation parsedCommandInvocation) {
        if (parsedCommandInvocation.getArgs().length < 1)
            return parsedCommandInvocation.getGuild();
        try {
            Guild guild = RubiconBot.getShardManager().getGuildById(parsedCommandInvocation.getArgs()[0]);
            guild.getName();
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException ignored) {
            try {
                Guild guild = RubiconBot.getGuildsByName(parsedCommandInvocation.getArgs()[0], true).get(0);
                guild.getName();
            } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException ignored1) {
                return null;
            }
            return RubiconBot.getGuildsByName(parsedCommandInvocation.getArgs()[0], true).get(0);
        }
        return RubiconBot.getShardManager().getGuildById(parsedCommandInvocation.getArgs()[0]);
    }

    private boolean hasIcon(Guild g) {
        try {
            String url = g.getIconUrl();
            url.toLowerCase();
        } catch (NullPointerException ignored) {
            return false;

        }
        return true;
    }

}