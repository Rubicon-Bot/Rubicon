package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandLeaveMessage extends CommandHandler {
    public CommandLeaveMessage() {
        super(new String[] {"leavemsg", "leavemessage"}, CommandCategory.SETTINGS, new PermissionRequirements(PermissionLevel.getByValue(2), "command.joinmsg"), "Set the server's leave message!", " <disable/Message(%user% for username, %guild% for guildname)>", false);
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length > 1)
            return createHelpMessage();
        String temp = "";
        for (int i = 0; i < parsedCommandInvocation.args.length; i++) {
            temp += " " + parsedCommandInvocation.args[i];
        }
        if(temp.equals("disable")){
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "leavemsg", "0");
            return new MessageBuilder().setEmbed(EmbedUtil.success("Disabled", "Succesfully disabled joinmessages").build()).build();
        }
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "leavemsg", temp.replaceFirst("null ", ""));
        String up = RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "leavemsg");
        return new MessageBuilder().setEmbed(new EmbedBuilder().setDescription(":white_check_mark:  Successfully set leavemessage to `" + up + "`!").build()).build();
    }
}
