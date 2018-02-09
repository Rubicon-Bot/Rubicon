package fun.rubicon.util;

/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package fun.rubicon.util
 */
public class StringUtil {
    /**
     * Check if String is numeric.
     *
     * @param str the String which should be tested.
     * @return true if String is Numeric else false.
     */
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Create Emoji out of Codepoints
     *
     * @param str the Codepoint.
     * @return the Emoji as a String.
     */
    public static String emojiOutCodePoint(String str) {
        int[] codepoints = {Integer.valueOf(str.replace("U+", "0x"))};
        String s = new String(codepoints, 0, codepoints.length);
        return s;
    }
}
