/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.listener.member;

import fun.rubicon.RubiconBot;
import fun.rubicon.commands.settings.CommandJoinMessage;
import fun.rubicon.core.ImageEditor;
import fun.rubicon.core.entities.RubiconGuild;
import fun.rubicon.core.entities.RubiconMember;
import fun.rubicon.util.BotListHandler;
import fun.rubicon.util.Logger;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class MemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        RubiconGuild rubiconGuild = RubiconGuild.fromGuild(event.getGuild());
        RubiconMember.fromMember(event.getMember());
        if (!RubiconBot.getConfiguration().getString("rubiconfun_token").isEmpty())
            BotListHandler.postRubiconFunUserCounts(false);

        new Thread(() -> {
            if (rubiconGuild.hasJoinMessagesEnabled()) {
                try {
                    CommandJoinMessage.JoinMessage joinMessage = rubiconGuild.getJoinMessage();
                    SafeMessage.sendMessage(event.getJDA().getTextChannelById(joinMessage.getChannelId()), joinMessage.getMessage()
                            .replace("%user%", event.getMember().getAsMention())
                            .replace("%guild%", event.getGuild().getName())
                            .replace("%count%", event.getGuild().getMembers().size() + ""));
                } catch (Exception e) {
                    rubiconGuild.deleteJoinMessage();
                }
            }
        }).start();

        new Thread(() -> {
            if (rubiconGuild.hasAutoroleEnabled()) {
                Role role = event.getGuild().getRoleById(rubiconGuild.getAutorole());
                if (role == null || !event.getGuild().getSelfMember().canInteract(role)) {
                    rubiconGuild.disableAutorole();
                    return;
                }
                event.getGuild().getController().addSingleRoleToMember(event.getMember(), role).queue();
            }
        }).start();

        new Thread(() -> {
            if (!rubiconGuild.hasJoinImagesEnabled())
                return;
            TextChannel channel = rubiconGuild.getGuild().getTextChannelById(rubiconGuild.getJoinImageChannel());
            if (channel == null) {
                rubiconGuild.disableJoinImages();
                return;
            }
            if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE) || !event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ATTACH_FILES)) {
                rubiconGuild.disableJoinImages();
                return;
            }
            try {
                if (event.getUser().getAvatarUrl() == null)
                    return;
                BufferedImage image = ImageIO.read(new URL("https://lordlee.de/pexels-photo.jpg").openStream());
                ImageEditor imageEditor = new ImageEditor(image);
                Font font = new Font("DejaVu Sans", Font.BOLD, 100);
                String name = event.getUser().getName();
                imageEditor.drawTextCentered(525, font, ((name.length() > 10) ? name.substring(0, 10) + "\n" + name.substring(10, name.length() - 1) : name));
                URL avatarCDNUrl = new URL(event.getUser().getAvatarUrl());
                URLConnection avatarCDN = avatarCDNUrl.openConnection();
                avatarCDN.setRequestProperty("User-Agent", "Rubicon Bot");
                BufferedImage avatarImage = ImageIO.read(avatarCDN.getInputStream());
                imageEditor.drawRoundImageCentered(100, 300, 300, avatarImage);
                channel.sendFile(imageEditor.getInputStream(), "join.jpg").queue();
            } catch (Exception e) {
                Logger.error(e);
            }
        }).start();

    }
}
