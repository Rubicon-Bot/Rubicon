/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Schlaubi
 */
@Deprecated
public class ConfigSetup {

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