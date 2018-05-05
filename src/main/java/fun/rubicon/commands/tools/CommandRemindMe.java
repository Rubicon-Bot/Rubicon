package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconRemind;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.SafeMessage;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.Date;

/**
 * @author Schlaubi / Michael Rittmeister
 */

public class CommandRemindMe extends CommandHandler {
    public CommandRemindMe() {
        super(new String[]{"remind", "reminder", "remindme"}, CommandCategory.TOOLS, new PermissionRequirements("remind", false, true), "I will remind you!", "<time> <message>/canel");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        if (args.length == 0)
            return createHelpMessage();
        RubiconRemind remind = new RubiconRemind(invocation.getAuthor());
        switch (args[0]) {
            case "cancel":
                if (!remind.exists())
                    return message(error(invocation.translate("command.remind.noreminder.title"), invocation.translate("command.remind.noreminder.description")));
                remind.cancel();
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.remind.cancelled.title"), invocation.translate("command.remind.canceled.description"))));
                break;
            case "info":
                if (!remind.exists())
                    return message(error(invocation.translate("command.remind.noreminder.title"), invocation.translate("command.remind.noreminder.description")));
                SafeMessage.sendMessage(invocation.getTextChannel(), formatReminder(remind.getAuthor().getName() + "'s Reminder", remind).build(), 7);
                break;
            default:
                Date expiry = StringUtil.parseDate(args[0]);
                if (expiry == null)
                    return message(error(invocation.translate("general.punishment.invalidnumber.title"), invocation.translate("general.punishment.invalidnumber.description")));
                remind = new RubiconRemind(invocation.getAuthor(), expiry, invocation.getArgsString().replace(args[0], ""));
                SafeMessage.sendMessage(invocation.getTextChannel(), formatReminder("Created reminder", remind).build());
                break;
        }
        return null;
    }

    private EmbedBuilder formatReminder(String title, RubiconRemind remind) {
        EmbedBuilder emb = new EmbedBuilder();
        emb.setTitle(title);
        emb.setDescription(String.format(TranslationUtil.translate(remind.getAuthor(), "reminder.embed.description"), remind.getRemindMessage()));
        emb.setFooter(String.format(TranslationUtil.translate(remind.getAuthor(), "reminder.embed.footer"), DateUtil.formatDate(remind.getRemindDate(), TranslationUtil.translate(remind.getAuthor(), "date.format"))), remind.getAuthor().getAvatarUrl());
        return emb;

    }
}
