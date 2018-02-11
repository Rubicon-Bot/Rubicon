package fun.rubicon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Setup {
    /**
     * Rubicon Discord bot
     *
     * @author Schlaubi
     * @copyright Rubicon Dev Team 2017
     * @license MIT License <http://rubicon.fun/license>
     * @package fun.rubicon.util
     */

    private static final BufferedReader sys_in;

    static {
        InputStreamReader isr = new InputStreamReader(System.in);
        sys_in = new BufferedReader(isr);
    }

    public static String prompt(String req) {
        String token;

        // prompt for token
        System.out.println("Enter your " + req + ":");


        try {
            // read and trim line
            String line = sys_in.readLine();
            token = line.trim();

            return token;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        // RAGEQUIT
        System.out.println("Exiting");
        Runtime.getRuntime().exit(1);
        return null;
    }
}
