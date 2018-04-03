package fun.rubicon.commands.botowner;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.FileUtil;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class CommandBotplay extends CommandHandler {

    public CommandBotplay() {
        super(new String[]{"botplay"}, CommandCategory.BOT_OWNER, new PermissionRequirements("botplay", true, false), "Customize the bots playing status.", "<type> <message>");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws UnsupportedEncodingException {
        File gameFile = FileUtil.createFileIfNotExist(new File("data/bot/settings", "status.game"));

        if (invocation.getArgs().length < 2)
            return createHelpMessage();
        final String type = invocation.getArgs()[0];
        if (type.equalsIgnoreCase("reset")) {
            FileUtil.writeToFile(gameFile, "");
            return EmbedUtil.message(EmbedUtil.success("Set playing status!", "Resetted."));
        }
        if (type.equalsIgnoreCase("stream") || type.equalsIgnoreCase("s")) {
            final String link = invocation.getArgs()[1];
            final String message = invocation.getArgsString().replaceFirst(type + " " + link + " ", "");
            RubiconBot.getShardManager().setGame(Game.streaming(message, link));
            FileUtil.writeToFile(gameFile, "s:" + link + ";;" + message);
            return EmbedUtil.message(EmbedUtil.success("Set playing status!", String.format("Rubicon is now streaming `%s`", message)));
        }
        final String message = invocation.getArgsString().replaceFirst(type + " ", "");
        switch (type.toLowerCase()) {
            case "play":
            case "p":
                RubiconBot.getShardManager().setGame(Game.playing(message));
                FileUtil.writeToFile(gameFile, "p:" + message);
                return EmbedUtil.message(EmbedUtil.success("Set playing status!", String.format("Rubicon is now playing `%s`", message)));
            case "watch":
            case "w":
                RubiconBot.getShardManager().setGame(Game.watching(message));
                FileUtil.writeToFile(gameFile, "w:" + message);
                return EmbedUtil.message(EmbedUtil.success("Set playing status!", String.format("Rubicon is now watching `%s`", message)));
            case "listen":
            case "l":
                RubiconBot.getShardManager().setGame(Game.listening(message));
                FileUtil.writeToFile(gameFile, "l:" + message);
                return EmbedUtil.message(EmbedUtil.success("Set playing status!", String.format("Rubicon is now listening `%s`", message)));
        }
        return createHelpMessage();
    }
}
