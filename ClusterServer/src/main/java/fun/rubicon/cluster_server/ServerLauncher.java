package fun.rubicon.cluster_server;

public class ServerLauncher {

    public static void main(String[] args) {
        printHeader();
        new Server();
    }

    private static void printHeader() {
        System.out.println("\n" +
                " _____       _     _                    _____ _           _            \n" +
                "|  __ \\     | |   (_)                  / ____| |         | |           \n" +
                "| |__) |   _| |__  _  ___ ___  _ __   | |    | |_   _ ___| |_ ___ _ __ \n" +
                "|  _  / | | | '_ \\| |/ __/ _ \\| '_ \\  | |    | | | | / __| __/ _ \\ '__|\n" +
                "| | \\ \\ |_| | |_) | | (_| (_) | | | | | |____| | |_| \\__ \\ ||  __/ |   \n" +
                "|_|  \\_\\__,_|_.__/|_|\\___\\___/|_| |_|  \\_____|_|\\__,_|___/\\__\\___|_|   \n" +
                "                                                                       \n");
        System.out.println("Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        System.out.println("Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        System.out.println("Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        System.out.println("\n");
    }
}
