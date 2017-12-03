package fun.rubicon.commands.admin;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.MySQL;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.admin
 */
public class CommandWarn extends CommandHandler{
    public CommandWarn(){
    super(new String[]{"warn"}, CommandCategory.MODERATION,new PermissionRequirements(2,"command.warn"),"Warn a User","warn <User Mention> <reason> ");
    }
    public static void WarnUser(User target, Guild guild,User author,String reason){
        MySQL sql = RubiconBot.getMySQL();
        sql.createWarning(guild,target,author,reason);
        int oldcase = Integer.parseInt(sql.getGuildValue(guild,"cases"));
        String nowcase = String.valueOf(oldcase+1);
        sql.updateGuildValue(guild,"cases",nowcase);
        if (!sql.getGuildValue(guild,"logchannel").equals("0")){
            TextChannel ch = guild.getTextChannelById(sql.getGuildValue(guild,"logchannel"));
            ch.sendMessage(EmbedUtil.info("**[CASE "+ sql.getGuildValue(guild,"cases")+ "]** Warned" + target.getAsMention(),"Warned by:"+author.getAsMention() + "\nReason:" + reason).build()).queue();
        }
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        if (parsedCommandInvocation.args.length<4){
            return new MessageBuilder().setEmbed(EmbedUtil.error("","Not enough arguments!\n"+getUsage()).build()).build();
        }
        if (parsedCommandInvocation.invocationMessage.getMentionedUsers().size()<1){
            return new MessageBuilder().setEmbed(EmbedUtil.error("","Please Mention someone!\n"+getUsage()).build()).build();
        }
        User targ = parsedCommandInvocation.invocationMessage.getMentionedUsers().get(0);
        Guild g = parsedCommandInvocation.invocationMessage.getGuild();
        User auth = parsedCommandInvocation.invocationMessage.getAuthor();
        String reas = "";
        for(int i=1;i<parsedCommandInvocation.args.length;i++){
            reas+= parsedCommandInvocation.args[i]+" ";
        }
        WarnUser(targ,g,auth,reas);
        return new MessageBuilder().setEmbed(EmbedUtil.success("Sucess","I warned" + targ.getAsMention()).build()).build();
    }
}
