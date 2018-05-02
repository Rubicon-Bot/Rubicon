package fun.rubicon.commands.tools;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.Colors;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandLeet extends CommandHandler {

    public CommandLeet() {
        super(new String[]{"1337", "leet"}, CommandCategory.TOOLS, new PermissionRequirements("1337", false, true), "Converts plain text into 1337.", "<text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0)
            return createHelpMessage();

        String text = invocation.getArgsString();
        text = text.replace("A", "4").replace("a", "4")
                .replace("B", "8").replace("b", "8")
                .replace("E", "3").replace("e", "3")
                .replace("G", "6").replace("g", "6")
                .replace("I", "!").replace("i", "!")
                .replace("J", "¿").replace("j", "¿")
                .replace("L", "1").replace("l", "1")
                .replace("O", "0").replace("o", "0")
                .replace("P", "9").replace("p", "9")
                .replace("S", "5").replace("S", "5")
                .replace("T", "7").replace("t", "7")
                .replace("Z", "z");

        if(text.length() > 1024)
            return createHelpMessage();
        SafeMessage.sendMessage(invocation.getTextChannel(), new EmbedBuilder().addField("Plain", invocation.getArgsString(), false).addField("1337", text, false).setColor(Colors.COLOR_SECONDARY).build());
        return null;
    }
}
