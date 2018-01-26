/*
 * Copyright (c) 2018 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.features.translation;

import fun.rubicon.RubiconBot;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.data.PermissionLevel;
import fun.rubicon.data.PermissionRequirements;
import fun.rubicon.data.UserPermissions;
import net.dv8tion.jda.core.entities.Message;

import java.util.Locale;

import static fun.rubicon.util.EmbedUtil.*;

public class LanguageCommandHandler extends CommandHandler {
    private final TranslationManager translationManager;

    protected LanguageCommandHandler(TranslationManager translationManager) {
        super(new String[]{"language", "lang"}, CommandCategory.SETTINGS,
                new PermissionRequirements(PermissionLevel.EVERYONE, "command.language"),
                "Change your language settings.", "[language-code]\nshow-languages");
        this.translationManager = translationManager;
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) {
        TranslationLocale locale = RubiconBot.sGetTranslations()
                .getUserLocale(invocation.invocationMessage.getAuthor());
        if (invocation.args.length == 0 || invocation.args[0].equalsIgnoreCase("get")) {
            return message(info(locale.getResourceBundle().getString("command.lang.get.title"),
                    commandFormat(invocation, locale, "command.lang.get.description"))
                    .addField(locale.getResourceBundle().getString("command.lang.get.field.currentsettings.title"),
                            locale.getResourceBundle().getString("command.lang.get.field.currentsettings.content")
                                    .replaceAll("%code%", locale.getLocaleCode())
                                    .replaceAll("%name%", locale.getLanguageName()), false));
        } else if (invocation.args[0].equalsIgnoreCase("show-languages")) {
            StringBuilder availableLanguages = new StringBuilder("— `"
                    + locale.getResourceBundle().getString("command.lang.list.tablehead.code") + "`  `"
                    + locale.getResourceBundle().getString("command.lang.list.tablehead.language") + "`\n");
            for(TranslationLocale availableLocale : translationManager.getLocales())
                availableLanguages.append("  \u2022 `").append(availableLocale.getLocaleCode()).append("`  ")
                        .append(availableLocale.getLanguageName()).append("\n");
            return message(info(locale.getResourceBundle().getString("command.lang.list.title"),
                    availableLanguages.toString()));
        } else {
            Locale newLocale = Locale.forLanguageTag(invocation.args[0]);
            TranslationLocale newTranslationLocale = translationManager.getTranslationLocaleByLocale(newLocale);
            if (newTranslationLocale == null)
                return message(error("Language not found", "A language with the tag `" +
                        invocation.args[0] + "` could not be found. Use `" + invocation.serverPrefix
                        + invocation.invocationCommand + " show-languages` for a list of supported languages."));
            else {
                translationManager.setUserLocale(invocation.invocationMessage.getAuthor(), newTranslationLocale);
                return message(success(newTranslationLocale.getResourceBundle().getString("command.lang.set.title"),
                        newTranslationLocale.getResourceBundle().getString("command.lang.set.description")
                                .replaceAll("%newlanguagename%", newTranslationLocale.getLanguageName())));
            }
        }
    }

    private String commandFormat(CommandManager.ParsedCommandInvocation invocation, TranslationLocale locale, String key) {
        return locale.getResourceBundle().getString(key)
                .replaceAll("%prefix%", invocation.serverPrefix)
                .replaceAll("%command%", invocation.invocationCommand);
    }
}
