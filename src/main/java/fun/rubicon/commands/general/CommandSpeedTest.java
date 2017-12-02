package fun.rubicon.commands.general;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fun.rubicon.command.Command;
import fun.rubicon.command.CommandCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;


/**
 * Rubicon Discord bot
 *
 * @author Leon Kappes / Lee
 * @copyright Rubicon Dev Team 2017
 * @license MIT License <http://rubicon.fun/license>
 * @package commands.general
 */
public class CommandSpeedTest extends Command{
    public CommandSpeedTest(String command, CommandCategory category) {
        super(command, category);
    }

    @Override
    protected void execute(String[] args, MessageReceivedEvent e) {
        //Set some Var´s and delete Message
        SpeedTestSocket DSpeed = new SpeedTestSocket();
        SpeedTestSocket USpeed = new SpeedTestSocket();
        StringBuilder sb = new StringBuilder();
        e.getMessage().delete().queue();
        Message msg = e.getTextChannel().sendMessage(new EmbedBuilder().setDescription("**Speedtest started...**\n\nTesting downstream...").build()).complete();
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
    }

    @Override
    public String getDescription() {
        return "Do a speedtest of the bot-connection.";
    }

    @Override
    public String getUsage() {
        return "speedtest";
    }

    @Override
    public int getPermissionLevel() {
        return 0;
    }
}
