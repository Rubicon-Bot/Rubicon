package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.HashMap;

public class CommandUnban extends CommandHandler {


    public CommandUnban() {
        super(new String[]{"unban"}, CommandCategory.MODERATION, new PermissionRequirements("command.unban", false, false), "Unban", "unban <id>", false);
    }


    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        Guild guild = parsedCommandInvocation.getGuild();
        if (args.length == 0)
            return createHelpMessage();
        HashMap<String, Guild.Ban> banned_users_IDS = new HashMap<>();
        guild.getBanList().complete().forEach(b -> banned_users_IDS.put(b.getUser().getId(), b));
        if (!banned_users_IDS.containsKey(args[0]))
            return new MessageBuilder().setEmbed(EmbedUtil.error("User not banned", "This user is not banned").build()).build();
        User user = banned_users_IDS.get(args[0]).getUser();
        try {
            guild.getController().unban(user).queue();
        } catch (PermissionException e) {
            return new MessageBuilder().setEmbed(EmbedUtil.error("No permission", "Unable to unban user").build()).build();
        }
        return new MessageBuilder().setEmbed(EmbedUtil.success("User unbanned", "Succesfully unbanned " + user.getAsMention()).build()).build();
    }
}
