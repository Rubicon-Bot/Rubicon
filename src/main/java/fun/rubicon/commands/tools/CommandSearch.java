package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandSearch extends CommandHandler {
    public CommandSearch() {
        super(new String[]{"search", "find"}, CommandCategory.TOOLS, new PermissionRequirements("search", false, true), "Search for Channels and users", "<query>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0)
            return createHelpMessage();
        Guild guild = invocation.getGuild();
        String query = invocation.getArgsString();
        StringBuilder textChannels = new StringBuilder();
        StringBuilder voiceChannels = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder roles = new StringBuilder();
        Message statusMessage = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), EmbedUtil.message(EmbedUtil.info(invocation.translate("command.search.searching.title"), invocation.translate("command.search.searching.textchannels"))));
        guild.getTextChannels().forEach(tc -> {
            if (tc.getName().toLowerCase().contains(query.toLowerCase()))
                textChannels.append(tc.getName()).append("(`").append(tc.getId()).append("`)\n");
        });
        statusMessage.editMessage(EmbedUtil.info(invocation.translate("command.search.searching.title"), invocation.translate("command.search.searching.voicechannels")).build()).queue();
        guild.getVoiceChannels().forEach(vc -> {
            if (vc.getName().toLowerCase().contains(query.toLowerCase()))
                voiceChannels.append(vc.getName()).append("(`").append(vc.getId()).append("`)\n");
        });
        statusMessage.editMessage(EmbedUtil.info(invocation.translate("command.search.searching.title"), invocation.translate("command.search.searching.members")).build()).queue();
        guild.getMembers().forEach(m -> {
            if (m.getUser().getName().toLowerCase().contains(query.toLowerCase()))
                members.append(m.getUser().getName()).append("(`").append(m.getUser().getId()).append("`)\n");
        });
        statusMessage.editMessage(EmbedUtil.info(invocation.translate("command.search.searching.title"), invocation.translate("command.search.searching.roles")).build()).queue();
        guild.getRoles().forEach(r -> {
            if (r.getName().toLowerCase().contains(query.toLowerCase()))
                roles.append(r.getName()).append("(`").append(r.getId()).append("`)\n");
        });

        EmbedBuilder emb = new EmbedBuilder().setColor(Colors.COLOR_PRIMARY).setTitle(invocation.translate("command.search.result"));
        if (!textChannels.toString().equals(""))
            emb.addField(invocation.translate("command.search.result.text"), textChannels.toString(), false);
        if (!voiceChannels.toString().equals(""))
            emb.addField(invocation.translate("command.search.result.voice"), voiceChannels.toString(), false);
        if (!members.toString().equals(""))
            emb.addField(invocation.translate("command.search.result.memb"), members.toString(), false);
        if (!roles.toString().equals(""))
            emb.addField(invocation.translate("command.search.result.role"), roles.toString(), false);
        if (emb.getFields().isEmpty())
            return message(EmbedUtil.error(invocation.translate("command.search.noresulst.title"), invocation.translate("command.search.noresulst.description")));
        return message(emb);
    }
}