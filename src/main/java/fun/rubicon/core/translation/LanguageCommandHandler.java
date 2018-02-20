/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.translation;

import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.util.Locale;

import static fun.rubicon.util.EmbedUtil.*;

public class LanguageCommandHandler extends CommandHandler {
    private final TranslationManager translationManager;

    protected LanguageCommandHandler(TranslationManager translationManager) {
        super(new String[]{"language", "lang"}, CommandCategory.SETTINGS,
                new PermissionRequirements("command.language", false, true),
                "Change your language settings.", "[language-code]\nshow-languages");
        this.translationManager = translationManager;
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        TranslationLocale locale = RubiconBot.sGetTranslations()
                .getUserLocale(invocation.getMessage().getAuthor());
        if (invocation.getArgs().length == 0 || invocation.getArgs()[0].equalsIgnoreCase("get")) {
            return message(info(locale.getResourceBundle().getString("command.lang.get.title"),
                    commandFormat(invocation, locale, "command.lang.get.description"))
                    .addField(locale.getResourceBundle().getString("command.lang.get.field.currentsettings.title"),
                            locale.getResourceBundle().getString("command.lang.get.field.currentsettings.content")
                                    .replaceAll("%code%", locale.getLocaleCode())
                                    .replaceAll("%name%", locale.getLanguageName()), false));
        } else if (invocation.getArgs()[0].equalsIgnoreCase("show-languages")) {
            StringBuilder availableLanguages = new StringBuilder("— `"
                    + locale.getResourceBundle().getString("command.lang.list.tablehead.code") + "`  `"
                    + locale.getResourceBundle().getString("command.lang.list.tablehead.language") + "`\n");
            for (TranslationLocale availableLocale : translationManager.getLocales())
                availableLanguages.append("  \u2022 `").append(availableLocale.getLocaleCode()).append("`  ")
                        .append(availableLocale.getLanguageName()).append("\n");
            return message(info(locale.getResourceBundle().getString("command.lang.list.title"),
                    availableLanguages.toString()));
        } else {
            Locale newLocale = Locale.forLanguageTag(invocation.getArgs()[0]);
            TranslationLocale newTranslationLocale = translationManager.getTranslationLocaleByLocale(newLocale);
            if (newTranslationLocale == null)
                return message(error("Language not found", "A language with the tag `" +
                        invocation.getArgs()[0] + "` could not be found. Use `" + invocation.getPrefix()
                        + invocation.getCommandInvocation() + " show-languages` for a list of supported languages."));
            else {
                RubiconUser.fromUser(invocation.getAuthor()).setLanguage(newTranslationLocale.getLocaleCode());
                return message(success(newTranslationLocale.getResourceBundle().getString("command.lang.set.title"),
                        newTranslationLocale.getResourceBundle().getString("command.lang.set.description")
                                .replaceAll("%newlanguagename%", newTranslationLocale.getLanguageName())));
            }
        }
    }

    private String commandFormat(CommandManager.ParsedCommandInvocation invocation, TranslationLocale locale, String key) {
        return locale.getResourceBundle().getString(key)
                .replaceAll("%prefix%", invocation.getPrefix())
                .replaceAll("%command%", invocation.getCommandInvocation());
    }
}
