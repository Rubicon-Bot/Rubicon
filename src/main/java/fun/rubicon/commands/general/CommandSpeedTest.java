/*
 * Copyright (c) 2017 Rubicon Bot Development Team
 *
 * Licensed under the MIT license. The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.commands.general;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;


public class CommandSpeedTest extends CommandHandler {

    public CommandSpeedTest() {
        super(new String[]{"speedtest", "st"}, CommandCategory.GENERAL, new PermissionRequirements("command.speedtest", false, true), "Do a speedtest of the bot-connection.", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation parsedCommandInvocation, UserPermissions userPermissions) {
        Message message = parsedCommandInvocation.getMessage();
        //Set some Var´s and delete Message
        SpeedTestSocket DSpeed = new SpeedTestSocket();
        SpeedTestSocket USpeed = new SpeedTestSocket();
        StringBuilder sb = new StringBuilder();
        Message msg = message.getTextChannel().sendMessage(new EmbedBuilder().setDescription("**Speedtest started...**\n\nTesting downstream...").build()).complete();
        //Test DownStream
        DSpeed.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                sb.append("Downstream:  " + Math.round(report.getTransferRateBit().floatValue() / 1024 / 1024) + " MBit/s\n");
                msg.editMessage(new EmbedBuilder().setDescription("**Speedtest starting...**\n\nTesting upstream...").build()).queue();
                USpeed.startUpload("http://2.testdebit.info/", 1000000);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
            }

            @Override
            public void onError(SpeedTestError speedTestError, String s) {
                System.out.println(speedTestError);
            }

        });
        //Test Upstream
        USpeed.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                sb.append("Upstream:    " + Math.round(report.getTransferRateBit().floatValue() / 1024 / 1024) + " MBit/s");
                msg.editMessage(new EmbedBuilder().setColor(new Color(72, 244, 66)).setDescription("**Test finished.**\n\n```" + sb.toString() + "```").build()).queue();
            }

            @Override
            public void onProgress(float v, SpeedTestReport speedTestReport) {

            }

            @Override
            public void onError(SpeedTestError speedTestError, String s) {
                System.out.println(speedTestError);
            }

        });
        //Url from where Test-File is downloaded
        DSpeed.startDownload("http://2.testdebit.info/10M.iso");
        return null;
    }
}
