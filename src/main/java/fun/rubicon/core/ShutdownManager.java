package fun.rubicon.core;

import fun.rubicon.RubiconBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ForYaSee / Yannick Seeger
 */
public class ShutdownManager {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownManager.class);

    public static void shutdown(Class clazz, String message) {
        logger.error(String.format("Executing shutdown.\n [%s] Reason: %s", clazz.getSimpleName(), message));
        RubiconBot.getInstance().shutdown();
        logger.info("Done.");
        System.exit(0);
    }
}
