package fun.rubicon.commands.general;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.sql.UserSQL;
import fun.rubicon.util.EmbedUtil;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CommandBio extends CommandHandler {

    public CommandBio() {
        super(new String[]{"bio"}, CommandCategory.GENERAL, new PermissionRequirements("command.bio", false, true), "Set your bio that is displayed in the rc!profile command. ", "set <text>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        if (invocation.getArgs().length == 0) {
            return createHelpMessage();
        }
        if (invocation.getArgs()[0].equalsIgnoreCase("set")) {
            UserSQL sql = new UserSQL(invocation.getAuthor());
            String bioText = invocation.getMessage().getContentDisplay().replace(invocation.getPrefix() + invocation.getCommandInvocation() + " set", "");
            if (bioText.toCharArray().length > 280) {
                return EmbedUtil.message(EmbedUtil.error("Error!", "Maximum char length is 280."));
            }
            bioText = filterWords(bioText);
            sql.set("bio", bioText);
            return EmbedUtil.message(EmbedUtil.success("Updated Bio!", "Successfully updated your bio."));
        }
        return createHelpMessage();
    }

    private String filterWords(String text) {
        String[] blacklist = {"penis", "dick", "cock", "cunt", "pussy", "nigga", "nigger", "trump", "porno", "porn", ".xxx"};
        for (String word : blacklist) {
            text = text.replace(word, getRandomReplaceWord());
        }
        return text;
    }

    private String getRandomReplaceWord() {
        String[] replaceWords = {"Sun", "Rainbow", "Sunshine", "xD", "lol", "heart", "Sunflowers"};
        int random = ThreadLocalRandom.current().nextInt(0, replaceWords.length - 1);
        return replaceWords[random];
    }
}
