/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.time.OffsetDateTime;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class DateUtil {

    public static String formatDate(OffsetDateTime time, String langFormat) {
        String date = langFormat.replaceAll("%dd%", time.getDayOfMonth() + "")
                .replaceAll("%MM%", time.getMonthValue() + "")
                .replaceAll("%yyyy%", time.getYear() + "");
        return date;
    }
}
