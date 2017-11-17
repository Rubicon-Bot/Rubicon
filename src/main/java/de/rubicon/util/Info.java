package de.rubicon.util;

import java.util.HashMap;

public class Info {


    public final static String BOT_TOKEN = "";
    public final static String BOT_ID = "380713705073147915";
    public final static String BOT_DEFAULT_PREFIX = "rc!";

    public final static String BOT_NAME = "Rubicon";
    public final static String BOT_VERSION = "0.1.0";
    private static HashMap<String, Long> BOT_AUTHORS;

    //NAMEN RICHTIG!
    public static void init() {
        BOT_AUTHORS = new HashMap<String, Long>();
        BOT_AUTHORS.put("ForYaSee", 227817074976751616L);
        BOT_AUTHORS.put("xEiiskeksx", 138014719582797824L);
        BOT_AUTHORS.put("Scryptex", 318773753796624394L);
        BOT_AUTHORS.put("Schlaubi", 264048760580079616L);
        BOT_AUTHORS.put("Robert", 148905646715043841L);
        BOT_AUTHORS.put("Lee", 153507094933274624L);
        BOT_AUTHORS.put("ForMoJa", 224528662710452224L);
    }
}
