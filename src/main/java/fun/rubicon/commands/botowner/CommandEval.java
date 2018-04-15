package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.MessageFormat;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2018
 * @license GPL-3.0 License <http://rubicon.fun/license>
 * @package fun.rubicon.commands.botowner
 */
public class CommandEval extends CommandHandler {

    public CommandEval() {
        super(new String[]{"eval","ausführen"}, CommandCategory.BOT_OWNER, new PermissionRequirements("eval", true, false), "Just Eval. Nothing to see here.", "Ähhm Nothing");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        String[] par = String.join(" ", parsedCommandInvocation.getArgs()).split("\\s+", 2);

        ScriptEngine se = new ScriptEngineManager().getEngineByName("Nashorn");
        try {
            se.eval("var imports = new JavaImporter(" +
                    "java.nio.file," +
                    "Packages.net.dv8tion.jda.core.Permission," +
                    "Packages.net.dv8tion.jda.core," +
                    "java.lang," +
                    "java.lang.management," +
                    "java.text," +
                    "java.sql," +
                    "java.util," +
                    "java.time," +
                    "Packages.com.sun.management" +
                    ");");
        } catch (ScriptException er) {
            er.printStackTrace();
        }
        se.put("jda", RubiconBot.getShardManager().getApplicationInfo().getJDA());
        se.put("guild", parsedCommandInvocation.getMessage().getGuild());
        se.put("channel", parsedCommandInvocation.getMessage().getChannel());
        se.put("message", parsedCommandInvocation.getMessage());
        se.put("author", parsedCommandInvocation.getMessage().getAuthor());

        String modified_msg = String.join(" ", parsedCommandInvocation.getArgs())
                .replace("getToken", "getTextChannelById(channel.getId()).sendMessage(\"UnsupportedOperationException(\\\"Nice try m8!\\\")\").queue");
            modified_msg = modified_msg.replaceAll("#", "().");

        try {
            Object out = se.eval(
                    "{" +
                            "with (imports) {" +
                            modified_msg +
                            "}" +
                            "};");

            if (out == null) {
                out = "Your action..<promise pending>";
            }

            parsedCommandInvocation.getMessage().getChannel().sendMessage(new StringBuilder().append("```Java\n").append(modified_msg)
                    .append("```Evaluated successfully:").toString()).queue();
            new MessageBuilder().appendCodeBlock(out.toString(), "Java").buildAll(MessageBuilder.SplitPolicy.NEWLINE, MessageBuilder.SplitPolicy.SPACE, MessageBuilder.SplitPolicy.ANYWHERE).forEach(message -> parsedCommandInvocation.getTextChannel().sendMessage(message).queue());
        } catch (ScriptException er) {
            parsedCommandInvocation.getMessage().getTextChannel().sendMessage(new StringBuilder().append("```Java\n").append(modified_msg)
                    .append(MessageFormat.format("``` ```Java\nAn exception was thrown:{0}```", er.toString())).toString()).queue();
        }
        return null;
    }
    }
