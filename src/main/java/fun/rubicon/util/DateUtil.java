/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class DateUtil {

    public static String formatDate(OffsetDateTime time, String langFormat) {
        String date = langFormat.replaceAll("%dd%", time.getDayOfMonth() + "")
                .replaceAll("%MM%", time.getMonthValue() + "")
                .replaceAll("%yyyy%", time.getYear() + "")
                .replaceAll("%hh%", time.getHour() + "")
                .replaceAll("%mm%", String.valueOf(time.getMinute()).length() == 1 ? "0" + time.getMinute() : time.getMinute() + "");
        return date;
    }

    public static String formatDate(Date time, String langFormat) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        String date = langFormat.replaceAll("%dd%", cal.get(Calendar.DAY_OF_MONTH) + "")
                .replaceAll("%MM%", cal.get(Calendar.MONTH) + 1 + "")
                .replaceAll("%yyyy%", cal.get(Calendar.YEAR) + "")
                .replaceAll("%hh%", cal.get(Calendar.HOUR_OF_DAY) + "")
                .replaceAll("%mm%", String.valueOf(cal.get(Calendar.MINUTE)).length() == 1 ? "0" + cal.get(Calendar.MINUTE) : cal.get(Calendar.MINUTE) + "");
        return date;
    }
}
