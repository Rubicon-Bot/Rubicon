/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.awt.*;

/**
 * Side-colors for embedded messages.
 *
 * @author Yannick Seeger / ForYaSee
 */
public class Colors {
    /**
     * Standard messages containing information like successful command responses.
     */
    public static Color COLOR_PRIMARY = new Color(88, 198, 33);
    /**
     * For messages containing additional information.
     */
    public static Color COLOR_SECONDARY = new Color(18, 109, 229);
    /**
     * For error messages that are not defined in further purpose.
     */
    public static Color COLOR_ERROR = new Color(229, 60, 18);
    /**
     * For permission-related error messages.
     */
    public static Color COLOR_NO_PERMISSION = new Color(75, 31, 94);
    /**
     * For (yet) unimplemented features.
     */
    public static Color COLOR_NOT_IMPLEMENTED = new Color(243, 156, 18);

    /**
     * For Premium messages
     */
    public static Color COLOR_PREMIUM = new Color(255, 215, 0);
}
