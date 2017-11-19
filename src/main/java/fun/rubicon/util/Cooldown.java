package fun.rubicon.util;

import java.util.ArrayList;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */
public class Cooldown {
    //Cooldown for Leveler
    public static ArrayList<String> ids = new ArrayList<>();


    public static boolean has(String id) {
        if (ids.contains(id)) {
            return true;
        }else {
            return false;
        }

    }

    public static void add(String id) {
        ids.add(id);
    }

    public static void remove(String id) {
        ids.remove(id);
    }


}
