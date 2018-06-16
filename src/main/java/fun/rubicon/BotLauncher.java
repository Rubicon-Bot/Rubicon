package fun.rubicon;

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import fun.rubicon.cluster.ClusterBuilder;
import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.command.ClusterCommandManager;
import fun.rubicon.cluster.commands.ClusterCommandHeartbeat;
import fun.rubicon.cluster.commands.ClusterCommandStartBot;
import fun.rubicon.io.Data;
import fun.rubicon.util.Info;
import net.dv8tion.jda.core.JDAInfo;

public class BotLauncher {

    private static ClusterCommandManager clusterCommandManager;
    private static ClusterClient clusterClient;

    public static void main(String[] args) {
        printHeader();


        //Initialise Config and Database
        Data.init();

        //Initialize ClusterCommands
        clusterCommandManager = new ClusterCommandManager();
        clusterCommandManager.addCommand(new ClusterCommandHeartbeat());
        clusterCommandManager.addCommand(new ClusterCommandStartBot());

        ClusterBuilder builder = new ClusterBuilder(Data.cfg().getString("cluster_host"), Data.cfg().getInt("cluster_port"));
        //Add Cluster Event Listeners
        builder.addListenerAdapter(clusterCommandManager);

        try {
            clusterClient = builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void printHeader() {
        System.out.println(
                " ______        _     _                  \n" +
                        "(_____ \\      | |   (_)                 \n" +
                        " _____) )_   _| |__  _  ____ ___  ____  \n" +
                        "|  __  /| | | |  _ \\| |/ ___) _ \\|  _ \\ \n" +
                        "| |  \\ \\| |_| | |_) ) ( (__| |_| | | | |\n" +
                        "|_|   |_|____/|____/|_|\\____)___/|_| |_|\n" +
                        "                                        \n"
        );
        System.out.println("Version: " + Info.BOT_VERSION);
        System.out.println("Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        System.out.println("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        System.out.println("Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        System.out.println("JDA: " + JDAInfo.VERSION);
        System.out.println("Lavaplayer: " + PlayerLibrary.VERSION);
        System.out.println("\n");
    }

    public static ClusterCommandManager getClusterCommandManager() {
        return clusterCommandManager;
    }

    public static ClusterClient getClusterClient() {
        return clusterClient;
    }
}
