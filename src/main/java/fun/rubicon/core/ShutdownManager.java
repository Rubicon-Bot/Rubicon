package fun.rubicon.core;

import fun.rubicon.RubiconBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ShutdownManager {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownManager.class);
    private static boolean shutdownInProgress = false;

    public static void shutdown(Class clazz, String message) {
        if (shutdownInProgress)
            return;
        shutdownInProgress = true;
        logger.error(String.format("Executing shutdown.\n [%s] Reason: %s", clazz.getName(), message));
        RubiconBot.getInstance().shutdown();
        System.exit(0);
    }
}
