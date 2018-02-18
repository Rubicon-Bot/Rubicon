package fun.rubicon.commands.settings;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.listener.ServerLogHandler;
import fun.rubicon.listener.ServerLogHandler.LogEventKeys;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.ServerLogSQL;
import fun.rubicon.util.Colors;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandLog extends CommandHandler {

    private CommandManager.ParsedCommandInvocation parsedCommandInvocation;

    private ServerLogSQL serverLogSQL;
    private String[] args;

    public CommandLog() {
        super(new String[]{"log", "logsettings"}, CommandCategory.SETTINGS, new PermissionRequirements("command.log", false, false), "Enable/Disable log settings", "list\n" +
                "channel <#channel>\n" +
                "join <enable/disable>\n" +
                "leave <enable/disable>\n" +
                "command <enable/disable>\n" +
                "ban <enable/disable>\n" +
                "role <enable/disable>\n" +
                "voice <enable/disable>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        this.parsedCommandInvocation = parsedCommandInvocation;
        this.args = parsedCommandInvocation.getArgs();
        this.serverLogSQL = new ServerLogSQL(parsedCommandInvocation.getMessage().getGuild());
        if (args.length == 0)
            return createHelpMessage();
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            return EmbedUtil.message(generateList());
        } else if (args.length == 2) {
            switch (args[0]) {
                case "channel":
                    if (parsedCommandInvocation.getMessage().getMentionedChannels().size() != 1)
                        return EmbedUtil.message(EmbedUtil.error("Error!", "You have to mention `one` channel."));
                    serverLogSQL.set("channel", parsedCommandInvocation.getMessage().getMentionedChannels().get(0).getId());
                    return EmbedUtil.message(EmbedUtil.success("Success!", "Successfully set logchannel to `" + parsedCommandInvocation.getMessage().getMentionedChannels().get(0).getName() + "`"));
                case "join":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.JOIN, args[1]));
                case "leave":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.LEAVE, args[1]));
                case "command":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.COMMAND, args[1]));
                case "ban":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.BAN, args[1]));
                case "role":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.ROLE, args[1]));
                case "voice":
                    return EmbedUtil.message(handleEventUpdate(LogEventKeys.VOICE, args[1]));
            }
        }
        return createHelpMessage();
    }

    private EmbedBuilder handleEventUpdate(ServerLogHandler.LogEventKeys event, String option) {
        if (option.equalsIgnoreCase("true") || option.equalsIgnoreCase("enable")) {
            serverLogSQL.set(event.getKey(), "true");
            return EmbedUtil.success("Success!", "Successfully **enabled** " + event.getDisplayname().toLowerCase() + " logging");
        } else if (option.equalsIgnoreCase("false") || option.equalsIgnoreCase("disable")) {
            serverLogSQL.set(event.getKey(), "false");
            return EmbedUtil.success("Success!", "Successfully **disabled** " + event.getDisplayname().toLowerCase() + " logging");
        } else
            return EmbedUtil.error("Error!", "Wrong arguments. Use `enable` or `disable`");
    }

    private EmbedBuilder generateList() {
        HashMap<LogEventKeys, Boolean> eventStats = new HashMap<>();

        for (LogEventKeys event : LogEventKeys.getAllKeys()) {
            String entry = serverLogSQL.get(event.getKey());
            if (entry.equalsIgnoreCase("true"))
                eventStats.put(event, true);
            else
                eventStats.put(event, false);
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Colors.COLOR_PRIMARY);
        builder.setAuthor("List of log events", null, parsedCommandInvocation.getMessage().getGuild().getIconUrl());

        for (Map.Entry entry : eventStats.entrySet()) {
            builder.addField(((LogEventKeys) entry.getKey()).getDisplayname() + " Event", ((boolean) entry.getValue()) ? "enabled" : "disabled", false);
        }
        builder.setDescription("Enable or disable an log event with `" + parsedCommandInvocation.getPrefix() + "log <event> <enable/disable>`");
        return builder;
    }
}
