package fun.rubicon.commands.tools;

import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.util.Colors;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class CommandServerInfo extends Command{
    public CommandServerInfo(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        Guild guild = e.getGuild();
        TextChannel channel = e.getTextChannel();

        StringBuilder roles = new StringBuilder();
        guild.getRoles().forEach(r -> {
            roles.append(r.getName()).append(", ");
        });

        EmbedBuilder serverinfo = new EmbedBuilder();
        serverinfo.setColor(Colors.COLOR_PRIMARY);
        serverinfo.setFooter(Info.EMBED_FOOTER, Info.ICON_URL);
        serverinfo.setTitle(":desktop: Serverinfo of " + guild.getName());
        serverinfo.setThumbnail(guild.getIconUrl());
        serverinfo.addField("ID", "`" + guild.getId() + "`", false);
        serverinfo.addField("Guildname", "`" + guild.getName() + "`", false);
        serverinfo.addField("Server region", guild.getRegionRaw(), false);
        serverinfo.addField("Members", String.valueOf(guild.getMembers().size()), false);
        serverinfo.addField("Textchannels", String.valueOf(guild.getTextChannels().size()), false);
        serverinfo.addField("Voicechannels", String.valueOf(guild.getVoiceChannels().size()), false);
        serverinfo.addField("Roles", String.valueOf(guild.getRoles().size()) + "\n ```" + roles.toString() + "```", false);
        serverinfo.addField("AFK Timeout", guild.getAfkTimeout().toString(), false);
        serverinfo.addField("Server owner", guild.getOwner().getUser().getName() + guild.getOwner().getUser().getDiscriminator(), false);
        serverinfo.addField("Server icon url", guild.getIconUrl(), false);
        channel.sendMessage(serverinfo.build()).queue();


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
