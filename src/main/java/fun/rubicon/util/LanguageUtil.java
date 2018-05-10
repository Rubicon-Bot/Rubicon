/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import java.util.ResourceBundle;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class LanguageUtil {

    public static String getString(ResourceBundle language, ResourceBundle defaultLanguage, String key) {
        try {
            return language.getString(key);
        } catch (Exception e) {
            return defaultLanguage.getString(key);
        }
    }
}
