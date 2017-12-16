package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command2.CommandHandler;
import fun.rubicon.command2.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;

/**
 * Rubicon Discord bot
 *
 * @author Michael Rittmeister / Schlaubi
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.admin
 */
public class CommandBan extends CommandHandler {
    public CommandBan() {
        super(new String[] {"ban"}, CommandCategory.MODERATION, new PermissionRequirements(PermissionLevel.WITH_PERMISSION, "command.ban"), "Bans a user from your server", "<@User>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message msg = parsedCommandInvocation.invocationMessage;
        if(msg.getMentionedUsers().isEmpty()){
            return new MessageBuilder().setEmbed(EmbedUtil.info("Usage", "ban <@User>").build()).build();
        }
        Member target = msg.getGuild().getMember(msg.getMentionedUsers().get(0));
        if(!msg.getGuild().getSelfMember().canInteract(target)){
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permissions", "Sorry I can't ban this User!!!").build()).build();
        } else {
            PrivateChannel channel = target.getUser().openPrivateChannel().complete();
            channel.sendMessage(EmbedUtil.success("Banned", "You got banned").build()).queue();
            msg.getGuild().getController().ban(target, 7).queue();
            return new MessageBuilder().setEmbed(EmbedUtil.success("Banned", "Successfully banned" + target.getAsMention()).build()).build();
        }
    }
}
