package fun.rubicon.commands.tools;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

public class CommandMoveAll extends CommandHandler{
    public CommandMoveAll() {
        super(new String[] {"moveall", "mvall", "mva"}, CommandCategory.TOOLS, new PermissionRequirements(PermissionLevel.ADMINISTRATOR, "command.moveall"), "Move all members in your channel into another channel", "moveall <Channel>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.args;
        Message message = parsedCommandInvocation.invocationMessage;
        if(args.length == 0){
            return createHelpMessage(parsedCommandInvocation);
        }
        if(!message.getMember().getVoiceState().inVoiceChannel())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Not connected", "Please connect to a voice channel to use this command").build()).build();
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < args.length; i++){
            name.append(args[i]);
        }
        List<VoiceChannel> channels = message.getGuild().getVoiceChannelsByName(name.toString(), false);
        if(channels.isEmpty())
            return new MessageBuilder().setEmbed(EmbedUtil.error("Channel not found", "This channel doesen't exits").build()).build();
        VoiceChannel channel = channels.get(0);
        if(channel.equals(message.getMember().getVoiceState().getChannel()))
            return new MessageBuilder().setEmbed(EmbedUtil.error("Same channel", "You are already connected to that channel").build()).build();
        GuildController controller = message.getGuild().getController();
        message.getMember().getVoiceState().getChannel().getMembers().forEach(m -> {
            controller.moveVoiceMember(m, channel).queue();
        });
        return new MessageBuilder().setEmbed(EmbedUtil.success("Connected", "Connected all users in your channel to `" + channel.getName() + "`").build()).build();
    }
}
