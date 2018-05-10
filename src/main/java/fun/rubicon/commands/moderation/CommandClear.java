package fun.rubicon.commands.moderation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class CommandClear extends CommandHandler {
    public CommandClear() {
        super(new String[]{"clear", "prune"}, CommandCategory.MODERATION, new PermissionRequirements("clear", false, false), "Deletes message", "<count> [@User]");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws UnsupportedEncodingException {
        String[] args = invocation.getArgs();
        if (args.length == 0)
            return createHelpMessage();

        if (!StringUtil.isNumeric(args[0]))
            return message(error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.clear.invalidcount.description")));

        int messageCount = Integer.parseInt(args[0]);
        User user = (invocation.getMessage().getMentionedUsers().isEmpty() ? null : invocation.getMessage().getMentionedUsers().get(0));

        if (messageCount < 2)
            return message(error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.clear.1message.description")));

        if (messageCount > 3000)
            return message(error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.clear.tomuchmessages.description")));

        if (!invocation.getSelfMember().hasPermission((Channel) invocation.getMessage().getChannel(), Permission.MESSAGE_MANAGE))
            return message(error(invocation.translate("command.money.give.selferror.title"), invocation.translate("command.clear.nopermission.description")));

        int deletedMessages = 0;

        while (messageCount != 0) {
            List<Message> messagesToDelete;
            if (messageCount > 100) {
                messagesToDelete = invocation.getTextChannel().getHistory().retrievePast(100).complete();
                messageCount -= 100;
            } else {
                messagesToDelete = invocation.getTextChannel().getHistory().retrievePast(messageCount).complete();
                messageCount = 0;
            }
            messagesToDelete = messagesToDelete.stream().filter(msg -> !msg.getCreationTime().isBefore(OffsetDateTime.now().minusWeeks(2))).collect(Collectors.toList());
            if (user != null)
                messagesToDelete = messagesToDelete.stream().filter(msg -> msg.getAuthor().equals(user)).collect(Collectors.toList());
            deletedMessages += messagesToDelete.size();
            if (messagesToDelete.size() > 1)
                invocation.getTextChannel().deleteMessages(messagesToDelete).complete();
        }

        return message(success(invocation.translate("command.clear.cleared.title"), String.format(invocation.translate("command.clear.cleared.description"), deletedMessages)));
    }
}
