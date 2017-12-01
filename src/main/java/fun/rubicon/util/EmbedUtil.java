/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.util;

import fun.rubicon.RubiconBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * Generates common embed messages.
 */
public class EmbedUtil {
    /**
     * Creates an embedded message indicating a successful feature execution.
     *
     * @param title       the title of the embedded.
     * @param description a more detailed description.
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder success(String title, String description) {
        return embed(":white_check_mark: " + title, description).setColor(Colors.COLOR_PRIMARY);
    }

    /**
     * Creates an embedded message with additional information.
     *
     * @param title       the title of the embedded.
     * @param description a more detailed description.
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder info(String title, String description) {
        return embed(":information_source: " + title, description).setColor(Colors.COLOR_SECONDARY);
    }

    /**
     * Creates an embedded unknown error message.
     *
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder error() {
        return error("Unknown error", "Am unknown error occurred.");
    }

    /**
     * Creates an embedded error message.
     *
     * @param title       the title of the embedded.
     * @param description the error description.
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder error(String title, String description) {
        return embed(":warning: " + title, description).setColor(Colors.COLOR_ERROR);
    }

    /**
     * Creates an embedded message.
     *
     * @param title       the title of the embedded.
     * @param description the description (main message).
     * @return the generated EmbedBuilder.
     */
    public static EmbedBuilder embed(String title, String description) {
        return new EmbedBuilder().setTitle(title).setDescription(description);
    }

    /**
     * Attaches a timestamp to an embedded message.
     *
     * @param embedBuilder the EmbedBuilder to attach a timestamp to.
     * @return the given EmbedBuilder with a timestamp.
     */
    public static EmbedBuilder withTimestamp(EmbedBuilder embedBuilder) {
        return embedBuilder.setFooter(RubiconBot.getNewTimestamp(), null);
    }

    /**
     * Converts an EmbedBuilder into a message.
     *
     * @param embedBuilder the EmbedBuilder that contains the embed data.
     * @return the compiled Message.
     */
    public static Message message(EmbedBuilder embedBuilder) {
        return new MessageBuilder().setEmbed(embedBuilder.build()).build();
    }
}
