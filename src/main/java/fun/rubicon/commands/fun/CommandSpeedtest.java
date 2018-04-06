package fun.rubicon.commands.fun;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fun.rubicon.command.CommandCategory;
import fun.rubicon.command.CommandHandler;
import fun.rubicon.command.CommandManager;
import fun.rubicon.permission.PermissionRequirements;
import fun.rubicon.permission.UserPermissions;
import fun.rubicon.util.EmbedUtil;
import fun.rubicon.util.SafeMessage;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.io.UnsupportedEncodingException;

public class CommandSpeedtest extends CommandHandler {
    public CommandSpeedtest() {
        super(new String[] {"speedtest"}, CommandCategory.FUN, new PermissionRequirements("speedtest", false, true), "Run speedtest on Rubicon's server", "");
    }

    @Override
    protected Message execute(CommandManager.ParsedCommandInvocation invocation, UserPermissions userPermissions) throws UnsupportedEncodingException {
        SpeedTestSocket Dsocket = new SpeedTestSocket();
        SpeedTestSocket Usocket = new SpeedTestSocket();
        StringBuilder result = new StringBuilder();
        Message resultMessage = SafeMessage.sendMessageBlocking(invocation.getTextChannel(), new MessageBuilder().setEmbed(EmbedUtil.info(invocation.translate("command.speedtest.running"), invocation.translate("command.speedtest.testing.downstream")).build()).build());
        Dsocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                result.append("Downstream: ").append(Math.round(report.getTransferRateBit().floatValue()) / 1024 / 1024).append("MBit/s").append("\n");
                resultMessage.editMessage(EmbedUtil.info(invocation.translate("command.speedtest.running"), invocation.translate("command.speedtest.testing.upstream")).build()).queue();
                Usocket.addSpeedTestListener(new ISpeedTestListener() {
                    @Override
                    public void onCompletion(SpeedTestReport report) {
                        result.append("Upstream: ").append(Math.round(report.getTransferRateBit().floatValue()) / 1024 / 1024).append("MBit/s");
                        resultMessage.editMessage(EmbedUtil.success(invocation.translate("command.speedtest.finished"), "```" + result.toString() + "```").build()).queue();
                    }

                    @Override
                    public void onProgress(float percent, SpeedTestReport report) {

                    }

                    @Override
                    public void onError(SpeedTestError speedTestError, String errorMessage) {
                        result.append("Uptream: `ERROR`");

                    }
                });
                Usocket.startUpload("http://ipv4.ikoula.testdebit.info/", 10);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {

            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                result.append("Downstream: `ERROR`");
            }
        });
        Dsocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
        return null;
    }
}
