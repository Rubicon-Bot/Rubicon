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

public class CommandToggleWelcome extends CommandHandler{
    public CommandToggleWelcome(){
        super(new String[]{"welmsg","twel","weltoggle","togglewelcome"}, CommandCategory.SETTINGS,new PermissionRequirements(2,"command.weltoggle"),"Toggle the Private Welcome Messages of the Bot","welmsg");
    }
    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.invocationMessage.getGuild(),"welmsg").equals("0")){
        RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(),"welmsg" ,"1");
        }else {
            RubiconBot.getMySQL().updateGuildValue(parsedCommandInvocation.invocationMessage.getGuild(),"welmsg" ,"0");
        }
        String onoroff ="";
        if (RubiconBot.getMySQL().getGuildValue(parsedCommandInvocation.invocationMessage.getGuild(),"welmsg").equals("0")){
            onoroff = "On";
        }else {
            onoroff = "Off";
        }
        return new MessageBuilder().setEmbed(EmbedUtil.success("Toggled Welcome Messages", "Set it to `" + onoroff + "`").build()).build();
    }
}
