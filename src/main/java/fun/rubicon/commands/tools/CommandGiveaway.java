package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.core.entities.RubiconGiveaway;
import fun.rubicon.core.translation.TranslationUtil;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.DateUtil;
import fun.rubicon.util.SafeMessage;
import fun.rubicon.util.StringUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Date;

/**
 * @author Leon Kappes / Lee (0.3%) Schlaubi / Michael Rittmeister (99.7%)
 * @copyright RubiconBot Dev Team 2018
 * @license GPL-3.0 License <http://rubicon.fun/license>
 */
public class CommandGiveaway extends CommandHandler {

    public CommandGiveaway() {
        super(new String[]{"giveaway"}, CommandCategory.TOOLS, new PermissionRequirements("giveaway", false, true), "Creates an automated giveaway users can take part in by reacting.", "<time> <prize>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        String[] args = invocation.getArgs();
        if (args.length == 0)
            return createHelpMessage();
        RubiconGiveaway giveaway = new RubiconGiveaway(invocation.getAuthor());
        switch (args[0]) {
            case "cancel":
                if (!giveaway.isExists())
                    return message(error());
                giveaway.cancel();
                SafeMessage.sendMessage(invocation.getTextChannel(), message(success(invocation.translate("command.giveaway.cancelled.title"), invocation.translate("command.giveaway.cancelled.description"))));
                break;
            case "info":
                if (!giveaway.isExists())
                    return message(error());
                TextChannel channel = invocation.getGuild().getTextChannelById(giveaway.getChannelId());
                if (channel == null) giveaway.cancel();

            default:
                if (args.length < 1)
                    return message(error());
                Date expiry = StringUtil.parseDate(args[0]);
                if (expiry == null)
                    return message(error(invocation.translate("general.punishment.invalidnumber.title"), invocation.translate("general.punishment.invalidnumber.description")));
                giveaway = new RubiconGiveaway(invocation.getAuthor(), expiry, invocation.getArgsString().replace(args[0], ""), invocation.getTextChannel(), 1);
                break;
        }
        return null;
    }


    public static EmbedBuilder formatGiveaway(RubiconGiveaway giveaway) {
        return new EmbedBuilder()
                .setColor(Colors.COLOR_PRIMARY)
                .setDescription("Price : `" + giveaway.getPrize() + "`")
                .setFooter(DateUtil.formatDate(giveaway.getExpirationDate(), TranslationUtil.translate(giveaway.getAuthor(), "date.format")), null);
    }
}