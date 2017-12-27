package fun.rubicon.util;

import java.io.File;
import java.io.IOException;

/**
 * Rubicon Discord bot
 *
 * @author Yannick Seeger / ForYaSee
 * @copyright RubiconBot Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */
public class FileUtil {

    public static File createFileIfNotExist(String path) {
        File file = new File(path);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
