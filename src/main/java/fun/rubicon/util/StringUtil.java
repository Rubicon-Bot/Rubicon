/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class StringUtil {

    public static boolean isNumeric(String s) {
        try {
            int i = Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Date parseDate(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int amount = parseInt(date);
        if (amount == 0) return null;
        if (date.contains("d"))
            cal.add(Calendar.DAY_OF_MONTH, amount);
        else if (date.contains("m"))
            cal.add(Calendar.MINUTE, amount);
        else if (date.contains("y"))
            cal.add(Calendar.YEAR, amount);
        else if (date.contains("M"))
            cal.add(Calendar.MONTH, amount);
        else if (date.contains("h"))
            cal.add(Calendar.HOUR_OF_DAY, amount);
        else if(date.contains("s"))
            cal.add(Calendar.SECOND, amount);
        else
            return null;
        return cal.getTime();
    }

    private static int parseInt(String integer) {
        try {
            return Integer.parseInt(integer.replace("d", "").replace("m", "").replace("y", "").replace("M", "").replace("h", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
