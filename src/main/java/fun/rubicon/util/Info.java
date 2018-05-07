/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.util.Date;


/**
 * General data object.
 *
 * @author ForYaSee, DerSchlaubi, LeeDJD, tr808axm
 */
public class Info {

    public final static String BOT_DEFAULT_PREFIX = "rc!";
    public final static String BOT_NAME = "RubiconBot";
    public final static String BOT_VERSION = "1.0.0";
    public final static String CONFIG_FILE = "config.json";

    public final static long RUBICON_SERVER = 381419503164325900L;

    /**
     * Bot author long ids.
     */
    public final static Long[] BOT_AUTHOR_IDS = {
            227817074976751616L, // ForYaSee
            153507094933274624L, // Lee
            264048760580079616L, // Schlaubi
            137263174675070976L,  // tr808axm
            137253345336229889L  // Lucsoft
    };

    public final static Long[] COMMUNITY_STAFF_TEAM = {
            401817301919465482L, //Skidder
            333220752117596160L, //BaseChip
            240797338430341120L, //Pilz
            235395943619493888L, //ls13game
            170821070004158464L, //Dlenny
            272397639855898624L  //Noooope
    };
    public static final long SUPPORT_ROLE = 442431704112562186L;
    public static final long BUG_ROLE = 442431609099255809L;

    public static Date lastRestart;
}
