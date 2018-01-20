package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandPremium extends CommandHandler {

    public CommandPremium() {
        super(new String[]{"premium"}, CommandCategory.GENERAL, new PermissionRequirements(PermissionLevel.EVERYONE, "command.premium"), "See your premium state or buy premium.",
                "| Shows current premium state\n" +
                        "buy | Buy premium");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] args = parsedCommandInvocation.getArgs();
        UserSQL userSQL = new UserSQL(parsedCommandInvocation.getAuthor());
        if (args.length == 0) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(parsedCommandInvocation.getAuthor().getName(), null, parsedCommandInvocation.getAuthor().getAvatarUrl());
            embedBuilder.setColor(Colors.COLOR_SECONDARY);
            String premiumEntry = userSQL.get("premium");
            if (premiumEntry.equalsIgnoreCase("false")) {
                embedBuilder.setDescription("No premium. \nBuy premium with `rc!premium buy` for 500k rubys.");
            } else {
                embedBuilder.setDescription("Premium is activated until " + parsePremiumEntry(premiumEntry));
            }
            parsedCommandInvocation.getTextChannel().sendMessage(embedBuilder.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.MINUTES));
            return null;
        }
        if (args[0].equalsIgnoreCase("buy")) {

        }
        return createHelpMessage();
    }

    public static String parsePremiumEntry(String entry) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(new Date(Long.parseLong(entry)));
    }
}
