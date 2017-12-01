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

public class CommandJoinMsg extends CommandHandler{
    public CommandJoinMsg(){
        super(new String[]{"joinmsg", "joinmessage", "joinnachricht"}, CommandCategory.SETTINGS,
                new PermissionRequirements(2, "command.joinmsg"),
                "Set the Server LogChannel!", "joinmsg <Message(%user% for username %guild% for guildname)>");
    }
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length<=1)
            return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(getUsage()).build()).build();
        String temp = "";
        for (int i = 1; i < parsedCommandInvocation.args.length; i++) {
            temp += " " + parsedCommandInvocation.args[i];
        }
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "joinmsg", temp.replaceFirst("null ", ""));
        String up = RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "joinmsg");
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":white_check_mark:  Successfully set joinmessage to `" + up + "`!").build()).build();
    }
}
