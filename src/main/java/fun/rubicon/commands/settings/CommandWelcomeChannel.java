package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandWelcomeChannel extends CommandHandler{

    public CommandWelcomeChannel(){
        super(new String[]{"channel", "welcomechannel", "welchannel", "joinchannel"}, CommandCategory.SETTINGS,
                new PermissionRequirements(3, "command.welcome"),
                "Set the Server Welcome Channel!", "channel #Channel");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.invocationMessage.getMentionedChannels().size() <= 0)
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(getUsage()).build()).build();
        String ch = parsedCommandInvocation.invocationMessage.getMentionedChannels().get(0).getId();
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "channel", ch);
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":white_check_mark: Successfully set the Joinmessagechannel!").build()).build();
    }
}
