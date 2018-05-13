package fun.rubicon.core;

import fun.rubicon.util.FileUtil;
import net.dv8tion.jda.core.entities.Game;

import java.io.File;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class GameStatusFileParser {

    public static Game parse() {
        final File gameFile = FileUtil.createFileIfNotExist(new File("data/bot/settings", "status.game"));
        final String content = FileUtil.readFromFile(gameFile);
        Game result = null;
        if (content.startsWith("p:")) {
            result = Game.playing(content.replaceFirst("p:", ""));
        } else if (content.startsWith("w:")) {
            result = Game.watching(content.replaceFirst("w:", ""));
        } else if (content.startsWith("l:")) {
            result = Game.listening(content.replaceFirst("l:", ""));
        } else if (content.startsWith("s:")) {
            final String replaced = content.replaceFirst("s:", "");
            final String[] splitted = replaced.split(";;");
            result = Game.streaming(splitted[1], splitted[0]);
        }
        return result;
    }
}
