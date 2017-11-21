package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandServerInfo extends Command{
    public CommandServerInfo(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Guild guild = e.getGuild();
        TextChannel channel = e.getTextChannel();

        StringBuilder rawRoles = new StringBuilder();
        guild.getRoles().forEach(r -> rawRoles.append(r.getName()).append(", "));
        StringBuilder roles = new StringBuilder(rawRoles.toString());
        roles.replace(rawRoles.lastIndexOf(","), roles.lastIndexOf(",") + 1, "" );
        EmbedBuilder serverInfo = new EmbedBuilder();
        serverInfo.setColor(Colors.COLOR_PRIMARY);
        serverInfo.setFooter(Info.EMBED_FOOTER, Info.ICON_URL);
        serverInfo.setTitle(":desktop: Serverinfo of " + guild.getName());
        serverInfo.setThumbnail(guild.getIconUrl());
        serverInfo.addField("ID", "`" + guild.getId() + "`", false);
        serverInfo.addField("Guildname", "`" + guild.getName() + "`", false);
        serverInfo.addField("Server region", guild.getRegion().toString(), false);
        serverInfo.addField("Members", String.valueOf(guild.getMembers().size()), false);
        serverInfo.addField("Textchannels", String.valueOf(guild.getTextChannels().size()), false);
        serverInfo.addField("Voicechannels", String.valueOf(guild.getVoiceChannels().size()), false);
        serverInfo.addField("Roles", String.valueOf(guild.getRoles().size()) + "\n ```" + roles.toString() + "```", false);
        serverInfo.addField("AFK Timeout", guild.getAfkTimeout().toString(), false);
        serverInfo.addField("Server owner", guild.getOwner().getUser().getName() + "#" + guild.getOwner().getUser().getDiscriminator(), false);
        serverInfo.addField("Server icon url", guild.getIconUrl(), false);
        channel.sendMessage(serverInfo.build()).queue();


    }

    @Override
    public String getDescription() {
        return "Returns some information about the current server";
    }

    @Override
    public String getUsage() {
        return "serverinfo";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
