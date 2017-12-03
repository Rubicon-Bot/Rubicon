package fun.rubicon.commands.settings;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.settings
 */
public class CommandAutorole extends CommandHandler{
    public CommandAutorole(){
        super(new String[]{"autorole","role"}, CommandCategory.ADMIN, new PermissionRequirements(2,"command.autorole"),"Set the Autorole.Triggers when a User Join your Guild","autorole <RoleMention or RoleName>");
    }
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length < 1) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("Error", getUsage()).build()).build();
        }
        if (parsedCommandInvocation.invocationMessage.getMentionedRoles().size() <1){
            String toset = parsedCommandInvocation.invocationMessage.getGuild().getRolesByName(parsedCommandInvocation.args[0],true).get(0).getId();
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "autorole", toset);
        }else {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(), "autorole", parsedCommandInvocation.invocationMessage.getMentionedRoles().get(0).getId());
        }
        return new MessageBuilder().setEmbed(EmbedUtil.success("Succes","Succesfully set the Autorole!").build()).build();
    }
}
